package com.nbufe.zfr.UniTakeout_server.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long shopId;
    private String shopName;
    private String status;
    private String contactName;
    private String contactPhone;
    private String address;
    private String payMethod;
    private BigDecimal goodsAmount;
    private BigDecimal deliveryFee;
    private BigDecimal totalAmount;
    private String remark;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 非数据库字段
    private List<OrderItem> items;
}

