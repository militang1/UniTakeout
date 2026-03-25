package com.nbufe.zfr.UniTakeout_server.service.impl;

import com.nbufe.zfr.UniTakeout_server.config.AIConfig;
import com.nbufe.zfr.UniTakeout_server.dto.AIAutoOrderDTO;
import com.nbufe.zfr.UniTakeout_server.dto.AIRecommendDTO;
import com.nbufe.zfr.UniTakeout_server.dto.OrderCreateDTO;
import com.nbufe.zfr.UniTakeout_server.dto.OrderItemDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import com.nbufe.zfr.UniTakeout_server.entity.Product;
import com.nbufe.zfr.UniTakeout_server.mapper.ProductMapper;
import com.nbufe.zfr.UniTakeout_server.service.AIService;
import com.nbufe.zfr.UniTakeout_server.service.OrderService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AIServiceImpl implements AIService {
    private final ProductMapper productMapper;
    private final OrderService orderService;
    private final AIConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public AIServiceImpl(ProductMapper productMapper, OrderService orderService, AIConfig config) {
        this.productMapper = productMapper;
        this.orderService = orderService;
        this.config = config;
    }

    public String chat(String userMessage) {
        // 1. 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getApiKey());

        // 2. 构造 messages
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "You are a helpful assistant"),
                Map.of("role", "user", "content", userMessage)
        );

        // 3. 构造请求体
        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("messages", messages);
        body.put("stream", false);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        // 4. 发送请求
        String url = config.getBaseUrl() + "/chat/completions";
        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        // 5. 解析返回
        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) response.getBody().get("choices");

        Map<String, Object> message =
                (Map<String, Object>) choices.get(0).get("message");

        return message.get("content").toString();
    }

    @Override
    public Map<String, Object> recommend(AIRecommendDTO dto) {
        // 简单的推荐逻辑：根据关键词和偏好推荐商品
        List<Product> products = productMapper.selectAll();
        
        List<Map<String, Object>> recommendations = new ArrayList<>();
        for (Product product : products) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", product.getId());
            item.put("name", product.getName());
            item.put("shopName", product.getShopName());
            item.put("price", product.getPrice());
            item.put("rating", product.getRating());
            item.put("image", product.getImage());
            item.put("reason", "符合您的需求");
            recommendations.add(item);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("recommendations", recommendations);
        result.put("message", "根据您的需求，我为您推荐以下美食");
        return result;
    }

    @Override
    public Order autoOrder(AIAutoOrderDTO dto) {
        // 简单的自动下单逻辑：根据预算推荐商品
        List<Product> products = productMapper.selectAll();
        
        List<OrderItemDTO> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal budget = new BigDecimal(30);
        
        for (Product product : products) {
            if (total.add(product.getPrice()).compareTo(budget) <= 0) {
                OrderItemDTO item = new OrderItemDTO();
                item.setProductId(product.getId());
                item.setName(product.getName());
                item.setQuantity(1);
                item.setPrice(product.getPrice());
                items.add(item);
                total = total.add(product.getPrice());
            }
        }
        
        if (items.isEmpty()) {
            throw new RuntimeException("未找到合适的商品");
        }
        
        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setShopId(products.get(0).getShopId());
        orderDTO.setShopName(products.get(0).getShopName());
        orderDTO.setContactName("用户");
        orderDTO.setContactPhone("13800138000");
        orderDTO.setAddress(dto.getAddress());
        orderDTO.setPayMethod("wechat");
        orderDTO.setItems(items);
        orderDTO.setGoodsAmount(total);
        orderDTO.setDeliveryFee(total.compareTo(new BigDecimal(30)) >= 0 ? BigDecimal.ZERO : new BigDecimal(3));
        orderDTO.setTotalAmount(orderDTO.getGoodsAmount().add(orderDTO.getDeliveryFee()));
        
        return orderService.create(dto.getUserId(), orderDTO);
    }

    @Override
    public Map<String, Object> agentSuggest(AIAutoOrderDTO dto) {
        List<Product> products = productMapper.selectAll();

        // 精简产品列表用于上下文（避免过多字段）
        List<Map<String, Object>> simpleProducts = new ArrayList<>();
        for (Product p : products) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("shopId", p.getShopId());
            m.put("shopName", p.getShopName());
            m.put("name", p.getName());
            m.put("price", p.getPrice());
            simpleProducts.add(m);
        }

        // 构造 system prompt，要求 AI 返回固定的 JSON
        String systemPrompt = "You are an assistant for a campus takeout system. " +
                "Given the product list and a user request, you should output a SINGLE JSON object and NOTHING else. " +
                "The JSON must have two fields: 'reply' (string) and 'order' (object). 'order' should follow the order create schema: " +
                "{shopId, shopName, contactName, contactPhone, address, payMethod, items: [{productId, name, quantity, price}], goodsAmount, deliveryFee, totalAmount, remark}. " +
                "All prices must be numbers. If you select items, ensure they exist in the provided product list. " +
                "Respond only with the JSON and do not include any explanation or markdown.";

        String userMessage = "User request: " + (dto.getQuery() == null ? "" : dto.getQuery()) + "\n" +
                "Address: " + (dto.getAddress() == null ? "" : dto.getAddress()) + "\n" +
                "Products: " + simpleProducts.toString() + "\n" +
                "Constraints: budget default 30 if not specified, try to choose suitable items and a shop.";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getApiKey());

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("messages", messages);
        body.put("stream", false);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String url = config.getBaseUrl() + "/chat/completions";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = message.get("content").toString();

        // 试图从 content 中提取 JSON 对象
        String jsonText = null;
        int first = content.indexOf('{');
        int last = content.lastIndexOf('}');
        if (first >= 0 && last >= 0 && last >= first) {
            jsonText = content.substring(first, last + 1);
        } else {
            throw new RuntimeException("AI 返回未包含 JSON: " + content);
        }

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        Map<String, Object> parsed;
        try {
            parsed = mapper.readValue(jsonText, Map.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("无法解析 AI 返回的 JSON: " + e.getMessage());
        }

        String reply = parsed.get("reply") == null ? null : parsed.get("reply").toString();

        // 构建 OrderCreateDTO 并校验/计算金额
        Map<String, Object> orderMap = (Map<String, Object>) parsed.get("order");
        if (orderMap == null || orderMap.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("reply", (reply == null || reply.isBlank()) ? "AI当前无法生成订单建议" : reply);
            result.put("order", null);
            return result;
        }

        List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) orderMap.get("items");
        List<OrderItemDTO> items = new ArrayList<>();
        BigDecimal goodsAmount = BigDecimal.ZERO;

        if (itemsRaw != null) {
            for (Map<String, Object> ir : itemsRaw) {
                Object pidObj = ir.get("productId");
                Long pid = null;
                if (pidObj instanceof Number) {
                    pid = ((Number) pidObj).longValue();
                } else if (pidObj instanceof String) {
                    try {
                        pid = Long.parseLong((String) pidObj);
                    } catch (Exception ignored) {
                    }
                }
                // 没有 productId 的条目无法下单，直接跳过
                if (pid == null) continue;

                OrderItemDTO it = new OrderItemDTO();
                it.setProductId(pid);
                it.setName((String) ir.get("name"));

                Object qtyObj = ir.get("quantity");
                int qty = 1;
                if (qtyObj instanceof Number) {
                    qty = ((Number) qtyObj).intValue();
                } else if (qtyObj instanceof String) {
                    try {
                        qty = Integer.parseInt((String) qtyObj);
                    } catch (Exception ignored) {
                    }
                }
                it.setQuantity(qty);

                BigDecimal price;
                if (ir.get("price") != null) {
                    price = new BigDecimal(ir.get("price").toString());
                } else {
                    // 尝试从产品库查价格
                    price = products.stream()
                            .filter(p -> p.getId().equals(it.getProductId()))
                            .map(Product::getPrice)
                            .findFirst()
                            .orElse(BigDecimal.ZERO);
                }
                it.setPrice(price);

                items.add(it);
                goodsAmount = goodsAmount.add(price.multiply(new BigDecimal(it.getQuantity())));
            }
        }

        // 没有可用商品时，也视为未生成订单
        if (items.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("reply", (reply == null || reply.isBlank()) ? "AI当前无法生成订单建议" : reply);
            result.put("order", null);
            return result;
        }

        OrderCreateDTO orderDTO = new OrderCreateDTO();
        Long shopIdFromJson = null;
        if (orderMap.get("shopId") != null) {
            Number n = (Number) orderMap.get("shopId");
            shopIdFromJson = n.longValue();
        }
        // orderMap 里缺失 shopId 时，从第一个商品推导
        Long shopIdFromItems = null;
        if (shopIdFromJson == null && items.get(0).getProductId() != null) {
            shopIdFromItems = products.stream()
                    .filter(p -> p.getId().equals(items.get(0).getProductId()))
                    .map(Product::getShopId)
                    .findFirst()
                    .orElse(null);
        }

        Long resolvedShopId = shopIdFromJson != null ? shopIdFromJson : shopIdFromItems;
        orderDTO.setShopId(resolvedShopId);

        String shopName = (String) orderMap.get("shopName");
        if (shopName == null && resolvedShopId != null) {
            shopName = products.stream()
                    .filter(p -> p.getShopId().equals(resolvedShopId))
                    .map(Product::getShopName)
                    .findFirst()
                    .orElse(null);
        }
        orderDTO.setShopName(shopName);

        orderDTO.setContactName(orderMap.get("contactName") == null ? "用户" : (String) orderMap.get("contactName"));
        orderDTO.setContactPhone(orderMap.get("contactPhone") == null ? "13800138000" : (String) orderMap.get("contactPhone"));
        orderDTO.setAddress(dto.getAddress() == null ? (String) orderMap.get("address") : dto.getAddress());
        orderDTO.setPayMethod(orderMap.get("payMethod") == null ? "wechat" : (String) orderMap.get("payMethod"));
        orderDTO.setItems(items);
        orderDTO.setGoodsAmount(goodsAmount);

        BigDecimal deliveryFee = goodsAmount.compareTo(new BigDecimal(30)) >= 0 ? BigDecimal.ZERO : new BigDecimal(3);
        orderDTO.setDeliveryFee(deliveryFee);
        orderDTO.setTotalAmount(goodsAmount.add(deliveryFee));
        orderDTO.setRemark(orderMap.get("remark") == null ? null : (String) orderMap.get("remark"));

        // 缺关键字段时，避免前端展示“假订单”，这里也回退为 order=null
        if (orderDTO.getShopId() == null || orderDTO.getShopName() == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("reply", (reply == null || reply.isBlank()) ? "AI当前无法生成订单建议" : reply);
            result.put("order", null);
            return result;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("reply", reply);
        result.put("order", orderDTO);
        System.out.println("AI 输出: " + result);
        return result;
    }

    @Override
    public SseEmitter agentSuggestStream(AIAutoOrderDTO dto) {
        SseEmitter emitter = new SseEmitter(0L); // 不超时，让前端等待直到生成完成

        // 用独立线程避免阻塞 Web 线程
        new Thread(() -> {
            HttpClient client = HttpClient.newBuilder().build();
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            List<Product> products = productMapper.selectAll();

            // 精简产品列表用于上下文（避免过多字段）
            List<Map<String, Object>> simpleProducts = new ArrayList<>();
            for (Product p : products) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", p.getId());
                m.put("shopId", p.getShopId());
                m.put("shopName", p.getShopName());
                m.put("name", p.getName());
                m.put("price", p.getPrice());
                simpleProducts.add(m);
            }

            // 关键：为了能够“流式输出 reply”，要求 AI 固定输出两个段落：
            // 1) REPLY: 一行文本（不包含换行）
            // 2) ORDER: JSON 对象或 null
            String systemPrompt = ""
                    + "You are an assistant for a campus takeout system. "
                    + "You must output ONLY the following two lines and nothing else. "
                    + "Line 1: start with 'REPLY:' followed by a single-line reply text (do not include newline characters). "
                    + "Line 2: start with 'ORDER:' followed by either a JSON object for order create schema, or the literal 'null'. "
                    + "The order JSON schema is: "
                    + "{shopId, shopName, contactName, contactPhone, address, payMethod, items: [{productId, name, quantity, price}], goodsAmount, deliveryFee, totalAmount, remark}. "
                    + "All prices must be numbers. "
                    + "If you cannot generate order, set ORDER: null.";

            String userMessage = "User request: " + (dto.getQuery() == null ? "" : dto.getQuery()) + "\n"
                    + "Address: " + (dto.getAddress() == null ? "" : dto.getAddress()) + "\n"
                    + "Products: " + simpleProducts.toString() + "\n"
                    + "Constraints: budget default 30 if not specified, try to choose suitable items and a shop.";

            try {
                String url = config.getBaseUrl() + "/chat/completions";

                Map<String, Object> body = new HashMap<>();
                body.put("model", config.getModel());
                body.put("messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ));
                body.put("stream", true);

                String bodyJson = mapper.writeValueAsString(body);

                HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + config.getApiKey())
                        .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                        .build();
                HttpResponse<java.io.InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8));
                StringBuilder fullContent = new StringBuilder();

                int replySentLen = 0;
                int replyStartIndex = -1;
                boolean reachedOrder = false;

                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    if (line.isBlank()) continue;

                    // OpenAI stream format: "data: {json}\n"
                    if (!line.startsWith("data:")) continue;
                    String dataStr = line.substring("data:".length()).trim();
                    if ("[DONE]".equals(dataStr)) break;
                    if (dataStr.isBlank()) continue;

                    Map<String, Object> chunk;
                    try {
                        chunk = mapper.readValue(dataStr, Map.class);
                    } catch (Exception ignored) {
                        continue;
                    }

                    List<Map<String, Object>> choices = (List<Map<String, Object>>) chunk.get("choices");
                    if (choices == null || choices.isEmpty()) continue;
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> delta = firstChoice == null ? null : (Map<String, Object>) firstChoice.get("delta");
                    if (delta == null) continue;

                    Object deltaContentObj = delta.get("content");
                    if (deltaContentObj == null) continue;
                    String deltaContent = deltaContentObj.toString();
                    if (deltaContent.isEmpty()) continue;

                    fullContent.append(deltaContent);

                    if (!reachedOrder) {
                        // 尽量从缓冲区里截取 REPLY 段落并“增量”推送给前端
                        if (replyStartIndex < 0) {
                            int idx = fullContent.indexOf("REPLY:");
                            if (idx >= 0) {
                                replyStartIndex = idx + "REPLY:".length();
                                // 跳过可能出现的空格
                                while (replyStartIndex < fullContent.length()
                                        && Character.isWhitespace(fullContent.charAt(replyStartIndex))) {
                                    replyStartIndex++;
                                }
                            }
                        }

                        if (replyStartIndex >= 0) {
                            int orderIdx = fullContent.indexOf("ORDER:");
                            int replyEnd = orderIdx >= 0 ? orderIdx : fullContent.length();
                            if (orderIdx >= 0) reachedOrder = true;

                            if (replyEnd > replyStartIndex) {
                                String replyPart = fullContent.substring(replyStartIndex, replyEnd);
                                if (replyPart.length() > replySentLen) {
                                    String newChunk = replyPart.substring(replySentLen);
                                    replySentLen = replyPart.length();
                                    emitter.send(SseEmitter.event().name("token").data(newChunk));
                                }
                            }
                        }
                    }
                }

                // 生成结束后，解析完整 REPLY/ORDER 并发送 final 事件
                String full = fullContent.toString();
                int rs = full.indexOf("REPLY:");
                int os = full.indexOf("ORDER:");
                String reply = "";
                String orderRaw = "null";

                if (rs >= 0 && os > rs) {
                    reply = full.substring(rs + "REPLY:".length(), os).trim();
                }
                if (os >= 0) {
                    orderRaw = full.substring(os + "ORDER:".length()).trim();
                }

                Object orderObj = null;
                if (orderRaw != null && !orderRaw.isBlank() && !"null".equals(orderRaw)) {
                    try {
                        // orderRaw 应该是 json object（或接近 json object）
                        Map<String, Object> orderMap = mapper.readValue(orderRaw, Map.class);
                        // items: [{productId, name, quantity, price}]
                        List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) orderMap.get("items");

                        List<OrderItemDTO> items = new ArrayList<>();
                        BigDecimal goodsAmount = BigDecimal.ZERO;

                        if (itemsRaw != null) {
                            for (Map<String, Object> ir : itemsRaw) {
                                Object pidObj = ir.get("productId");
                                Long pid = null;
                                if (pidObj instanceof Number) {
                                    pid = ((Number) pidObj).longValue();
                                } else if (pidObj instanceof String) {
                                    try {
                                        pid = Long.parseLong((String) pidObj);
                                    } catch (Exception ignored) {
                                    }
                                }
                                if (pid == null) continue;

                                OrderItemDTO it = new OrderItemDTO();
                                it.setProductId(pid);
                                it.setName((String) ir.get("name"));

                                Object qtyObj = ir.get("quantity");
                                int qty = 1;
                                if (qtyObj instanceof Number) {
                                    qty = ((Number) qtyObj).intValue();
                                } else if (qtyObj instanceof String) {
                                    try {
                                        qty = Integer.parseInt((String) qtyObj);
                                    } catch (Exception ignored) {
                                    }
                                }
                                it.setQuantity(qty);

                                BigDecimal price;
                                if (ir.get("price") != null) {
                                    price = new BigDecimal(ir.get("price").toString());
                                } else {
                                    // 尝试从产品库查价格
                                    price = products.stream()
                                            .filter(p -> p.getId().equals(it.getProductId()))
                                            .map(Product::getPrice)
                                            .findFirst()
                                            .orElse(BigDecimal.ZERO);
                                }
                                it.setPrice(price);

                                items.add(it);
                                goodsAmount = goodsAmount.add(price.multiply(new BigDecimal(it.getQuantity())));
                            }
                        }

                        if (!items.isEmpty()) {
                            OrderCreateDTO orderDTO = new OrderCreateDTO();
                            Long shopIdFromJson = null;
                            if (orderMap.get("shopId") != null) {
                                Number n = (Number) orderMap.get("shopId");
                                shopIdFromJson = n.longValue();
                            }
                            Long shopIdFromItems = null;
                            if (shopIdFromJson == null && items.get(0).getProductId() != null) {
                                shopIdFromItems = products.stream()
                                        .filter(p -> p.getId().equals(items.get(0).getProductId()))
                                        .map(Product::getShopId)
                                        .findFirst()
                                        .orElse(null);
                            }
                            Long resolvedShopId = shopIdFromJson != null ? shopIdFromJson : shopIdFromItems;

                            orderDTO.setShopId(resolvedShopId);

                            String shopName = (String) orderMap.get("shopName");
                            if (shopName == null && resolvedShopId != null) {
                                shopName = products.stream()
                                        .filter(p -> p.getShopId().equals(resolvedShopId))
                                        .map(Product::getShopName)
                                        .findFirst()
                                        .orElse(null);
                            }
                            orderDTO.setShopName(shopName);

                            orderDTO.setContactName(orderMap.get("contactName") == null ? "用户" : (String) orderMap.get("contactName"));
                            orderDTO.setContactPhone(orderMap.get("contactPhone") == null ? "13800138000" : (String) orderMap.get("contactPhone"));
                            orderDTO.setAddress(dto.getAddress() == null ? (String) orderMap.get("address") : dto.getAddress());
                            orderDTO.setPayMethod(orderMap.get("payMethod") == null ? "wechat" : (String) orderMap.get("payMethod"));
                            orderDTO.setItems(items);
                            orderDTO.setGoodsAmount(goodsAmount);

                            BigDecimal deliveryFee = goodsAmount.compareTo(new BigDecimal(30)) >= 0 ? BigDecimal.ZERO : new BigDecimal(3);
                            orderDTO.setDeliveryFee(deliveryFee);
                            orderDTO.setTotalAmount(goodsAmount.add(deliveryFee));
                            orderDTO.setRemark(orderMap.get("remark") == null ? null : (String) orderMap.get("remark"));

                            // 缺关键字段时，回退为 order=null，防止前端展示“假订单”
                            if (orderDTO.getShopId() == null || orderDTO.getShopName() == null) {
                                orderObj = null;
                            } else {
                                orderObj = orderDTO;
                            }
                        }
                    } catch (Exception ignored) {
                        orderObj = null;
                    }
                }

                Map<String, Object> finalResult = new HashMap<>();
                finalResult.put("reply", reply == null ? "" : reply);
                finalResult.put("order", orderObj);
                finalResult.put("recommendations", null);

                emitter.send(SseEmitter.event().name("final").data(mapper.writeValueAsString(finalResult)));
                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                } catch (Exception ignored) {
                }
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}

