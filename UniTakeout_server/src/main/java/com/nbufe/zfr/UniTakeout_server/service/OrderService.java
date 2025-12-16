package com.nbufe.zfr.UniTakeout_server.service;

import com.nbufe.zfr.UniTakeout_server.dto.OrderCreateDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;

public interface OrderService {
    Order create(Long userId, OrderCreateDTO dto);
    PageResult<Order> getList(Long userId, String status, Integer page, Integer pageSize);
    Order getById(Long id);
    void cancel(Long id, String reason);
}

