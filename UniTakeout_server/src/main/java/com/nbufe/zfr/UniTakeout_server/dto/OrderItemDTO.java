package com.nbufe.zfr.UniTakeout_server.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
}

