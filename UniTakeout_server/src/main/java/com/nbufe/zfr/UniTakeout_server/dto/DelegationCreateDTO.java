package com.nbufe.zfr.UniTakeout_server.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DelegationCreateDTO {
    private String type;
    private String title;
    private String description;
    private Long shopId;
    private String shopName;
    private BigDecimal reward;
    private List<Object> items;
}

