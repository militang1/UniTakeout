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
}

