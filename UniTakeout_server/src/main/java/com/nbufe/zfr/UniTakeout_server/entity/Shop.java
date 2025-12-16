package com.nbufe.zfr.UniTakeout_server.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Shop {
    private Long id;
    private String name;
    private String description;
    private String image;
    private BigDecimal rating;
    private Integer monthlySales;
    private Integer deliveryTime;
    private BigDecimal distance;
    private String businessHours;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 非数据库字段
    private List<String> tags;
}

