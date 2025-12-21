package com.nbufe.zfr.UniTakeout_server.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminShopVO {
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
    private List<String> tags;
}






