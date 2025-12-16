package com.nbufe.zfr.UniTakeout_server.service;

import com.nbufe.zfr.UniTakeout_server.dto.AIAutoOrderDTO;
import com.nbufe.zfr.UniTakeout_server.dto.AIRecommendDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import java.util.Map;

public interface AIService {
    Map<String, Object> recommend(AIRecommendDTO dto);
    Order autoOrder(AIAutoOrderDTO dto);
}

