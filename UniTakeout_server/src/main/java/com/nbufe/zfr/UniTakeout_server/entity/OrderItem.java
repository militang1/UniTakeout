package com.nbufe.zfr.UniTakeout_server.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime createTime;
}

