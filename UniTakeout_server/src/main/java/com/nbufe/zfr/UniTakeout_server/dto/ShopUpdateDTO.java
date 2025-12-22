package com.nbufe.zfr.UniTakeout_server.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ShopUpdateDTO {
    private String name;
    private String description;
    private String image;
    private Integer deliveryTime;
    private BigDecimal distance;
    private String businessHours;
}








