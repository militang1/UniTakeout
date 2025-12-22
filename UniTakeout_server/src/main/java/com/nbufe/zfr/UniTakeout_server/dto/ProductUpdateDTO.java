package com.nbufe.zfr.UniTakeout_server.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductUpdateDTO {
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
}








