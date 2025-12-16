package com.nbufe.zfr.UniTakeout_server.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private Long shopId;
    private String name;
    private Integer sortOrder;
    private LocalDateTime createTime;
}

