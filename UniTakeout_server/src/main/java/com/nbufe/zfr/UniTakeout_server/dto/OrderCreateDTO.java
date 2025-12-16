package com.nbufe.zfr.UniTakeout_server.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateDTO {
    private Long shopId;
    private String shopName;
    private String contactName;
    private String contactPhone;
    private String address;
    private String payMethod;
    private List<OrderItemDTO> items;
    private BigDecimal goodsAmount;
    private BigDecimal deliveryFee;
    private BigDecimal totalAmount;
    private String remark;
}

