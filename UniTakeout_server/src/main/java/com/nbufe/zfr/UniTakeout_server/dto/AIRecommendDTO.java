package com.nbufe.zfr.UniTakeout_server.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AIRecommendDTO {
    private String query;
    private Long userId;
    private Map<String, Object> preferences;
}

