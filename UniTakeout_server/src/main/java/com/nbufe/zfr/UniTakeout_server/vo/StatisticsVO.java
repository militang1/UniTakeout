package com.nbufe.zfr.UniTakeout_server.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StatisticsVO {
    private Long totalOrders;
    private Long todayOrders;
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private Long totalProducts;
    private Integer pendingOrders;
}






