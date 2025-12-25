package com.nbufe.zfr.UniTakeout_server.service.impl;

import com.nbufe.zfr.UniTakeout_server.common.Constants;
import com.nbufe.zfr.UniTakeout_server.dto.OrderCreateDTO;
import com.nbufe.zfr.UniTakeout_server.dto.OrderItemDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import com.nbufe.zfr.UniTakeout_server.entity.OrderItem;
import com.nbufe.zfr.UniTakeout_server.mapper.OrderItemMapper;
import com.nbufe.zfr.UniTakeout_server.mapper.OrderMapper;
import com.nbufe.zfr.UniTakeout_server.service.OrderService;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nbufe.zfr.UniTakeout_server.websocket.WebSocketServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional
    public Order create(Long userId, OrderCreateDTO dto) {
        Order order = new Order();
        order.setUserId(userId);
        order.setShopId(dto.getShopId());
        order.setShopName(dto.getShopName());
        order.setStatus(Constants.ORDER_STATUS_PENDING);
        order.setContactName(dto.getContactName());
        order.setContactPhone(dto.getContactPhone());
        order.setAddress(dto.getAddress());
        order.setPayMethod(dto.getPayMethod());
        order.setGoodsAmount(dto.getGoodsAmount());
        order.setDeliveryFee(dto.getDeliveryFee());
        order.setTotalAmount(dto.getTotalAmount());
        order.setRemark(dto.getRemark());
        
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
                        String.format("%04d", (int)(Math.random() * 10000));
        order.setOrderNo(orderNo);
        
        orderMapper.insert(order);
        
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemDTO itemDTO : dto.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(itemDTO.getProductId());
            item.setName(itemDTO.getName());
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(itemDTO.getPrice());
            items.add(item);
        }
        orderItemMapper.insertBatch(items);
        
        order.setItems(items);

        // 推送 WebSocket 通知给商家端
        try {
            WebSocketServer.sendToAllClient("您有新的订单，请及时接单");
            logger.info("已推送新订单通知，orderNo={}", orderNo);
        } catch (Exception e) {
            logger.error("推送新订单通知失败", e);
        }

        return order;
    }

    @Override
    public PageResult<Order> getList(Long userId, String status, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        
        Integer offset = (page - 1) * pageSize;
        List<Order> list = orderMapper.selectByUserId(userId, status, offset, pageSize);
        
        for (Order order : list) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
        }
        
        Long total = orderMapper.countByUserId(userId, status);
        
        PageResult<Order> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public Order getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order != null) {
            order.setItems(orderItemMapper.selectByOrderId(id));
        }
        return order;
    }

    @Override
    @Transactional
    public void cancel(Long id, String reason) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!Constants.ORDER_STATUS_PENDING.equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }
        order.setStatus(Constants.ORDER_STATUS_CANCELLED);
        orderMapper.update(order);
    }
}

