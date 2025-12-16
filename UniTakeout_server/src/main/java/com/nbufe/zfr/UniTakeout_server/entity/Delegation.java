package com.nbufe.zfr.UniTakeout_server.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Delegation {
    private Long id;
    private String type;
    private Long userId;
    private String userName;
    private String title;
    private String description;
    private Long shopId;
    private String shopName;
    private BigDecimal reward;
    private String status;
    private Long acceptedBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 非数据库字段
    private List<Object> items;
}

