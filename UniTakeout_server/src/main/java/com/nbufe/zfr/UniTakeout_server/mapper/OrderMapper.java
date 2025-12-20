package com.nbufe.zfr.UniTakeout_server.mapper;

import com.nbufe.zfr.UniTakeout_server.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrderMapper {
    Order selectById(Long id);
    Order selectByOrderNo(String orderNo);
    List<Order> selectByUserId(Long userId, String status, Integer offset, Integer limit);
    Long countByUserId(Long userId, String status);
    List<Order> selectByShopId(Long shopId, String status, Integer offset, Integer limit);
    Long countByShopId(Long shopId, String status);
    void insert(Order order);
    void update(Order order);
}

