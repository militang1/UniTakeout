package com.nbufe.zfr.UniTakeout_server.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private Long shopId;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private Integer sales;
    private BigDecimal rating;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 非数据库字段
    private String categoryName;
    private String shopName;
}

