package com.nbufe.zfr.UniTakeout_server.service;

import com.nbufe.zfr.UniTakeout_server.dto.AIAutoOrderDTO;
import com.nbufe.zfr.UniTakeout_server.dto.AIRecommendDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;

public interface AIService {

    Map<String, Object> recommend(AIRecommendDTO dto);
    Order autoOrder(AIAutoOrderDTO dto);

    String chat(String message);

    // 使用 AI agent 给出订单建议（不直接创建订单），返回包含 reply 与 order 的固定 JSON 结构
    Map<String, Object> agentSuggest(AIAutoOrderDTO dto);

    // 使用 SSE 流式输出 AI 对话（先流式返回 reply 文本，最后返回完整 {reply, order}）
    SseEmitter agentSuggestStream(AIAutoOrderDTO dto);
}

