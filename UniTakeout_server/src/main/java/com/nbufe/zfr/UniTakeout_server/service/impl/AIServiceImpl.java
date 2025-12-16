package com.nbufe.zfr.UniTakeout_server.service.impl;

import com.nbufe.zfr.UniTakeout_server.dto.AIAutoOrderDTO;
import com.nbufe.zfr.UniTakeout_server.dto.AIRecommendDTO;
import com.nbufe.zfr.UniTakeout_server.dto.OrderCreateDTO;
import com.nbufe.zfr.UniTakeout_server.dto.OrderItemDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import com.nbufe.zfr.UniTakeout_server.entity.Product;
import com.nbufe.zfr.UniTakeout_server.mapper.ProductMapper;
import com.nbufe.zfr.UniTakeout_server.service.AIService;
import com.nbufe.zfr.UniTakeout_server.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AIServiceImpl implements AIService {
    private final ProductMapper productMapper;
    private final OrderService orderService;

    public AIServiceImpl(ProductMapper productMapper, OrderService orderService) {
        this.productMapper = productMapper;
        this.orderService = orderService;
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

