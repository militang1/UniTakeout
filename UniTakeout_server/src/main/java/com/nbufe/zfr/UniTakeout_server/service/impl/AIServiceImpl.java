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

import java.math.BigDecimal;
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

        // 构建 OrderCreateDTO 并校验/计算金额
        Map<String, Object> orderMap = (Map<String, Object>) parsed.get("order");
        if (orderMap == null) {
            throw new RuntimeException("AI 返回的 JSON 不包含 order 字段");
        }

        OrderCreateDTO orderDTO = new OrderCreateDTO();
        if (orderMap.get("shopId") != null) {
            Number n = (Number) orderMap.get("shopId");
            orderDTO.setShopId(n.longValue());
        }
        orderDTO.setShopName((String) orderMap.get("shopName"));
        orderDTO.setContactName(orderMap.get("contactName") == null ? "用户" : (String) orderMap.get("contactName"));
        orderDTO.setContactPhone(orderMap.get("contactPhone") == null ? "13800138000" : (String) orderMap.get("contactPhone"));
        orderDTO.setAddress(dto.getAddress() == null ? (String) orderMap.get("address") : dto.getAddress());
        orderDTO.setPayMethod(orderMap.get("payMethod") == null ? "wechat" : (String) orderMap.get("payMethod"));

        List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) orderMap.get("items");
        List<OrderItemDTO> items = new ArrayList<>();
        BigDecimal goodsAmount = BigDecimal.ZERO;
        if (itemsRaw != null) {
            for (Map<String, Object> ir : itemsRaw) {
                OrderItemDTO it = new OrderItemDTO();
                if (ir.get("productId") != null) {
                    Number pid = (Number) ir.get("productId");
                    it.setProductId(pid.longValue());
                }
                it.setName((String) ir.get("name"));
                Integer qty = ir.get("quantity") == null ? 1 : ((Number) ir.get("quantity")).intValue();
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

        orderDTO.setItems(items);
        orderDTO.setGoodsAmount(goodsAmount);
        BigDecimal deliveryFee = goodsAmount.compareTo(new BigDecimal(30)) >= 0 ? BigDecimal.ZERO : new BigDecimal(3);
        orderDTO.setDeliveryFee(deliveryFee);
        orderDTO.setTotalAmount(goodsAmount.add(deliveryFee));
        orderDTO.setRemark(orderMap.get("remark") == null ? null : (String) orderMap.get("remark"));

        Map<String, Object> result = new HashMap<>();
        result.put("reply", parsed.get("reply"));
        result.put("order", orderDTO);
        System.out.println("AI 输出: " + result);
        return result;
    }
}

