package com.nbufe.zfr.UniTakeout_server.controller.user;

import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.dto.AIAutoOrderDTO;
import com.nbufe.zfr.UniTakeout_server.dto.AIRecommendDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import com.nbufe.zfr.UniTakeout_server.service.AIService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/recommend")
    public Result<Map<String, Object>> recommend(@RequestBody AIRecommendDTO dto) {
        try {
            Map<String, Object> result = aiService.recommend(dto);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/auto-order")
    public Result<Order> autoOrder(@RequestBody AIAutoOrderDTO dto) {
        try {
            Order order = aiService.autoOrder(dto);
            return Result.success("订单已创建", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        String reply = aiService.chat(message);
        return Map.of("reply", reply);
    }

    /**
     * POST /api/ai/agent-suggest
     * 使用 AI agent 分析用户指令并返回固定 JSON，包含 reply 与 order（OrderCreateDTO 结构）
     */
    @PostMapping("/agent-suggest")
    public Result<Map<String, Object>> agentSuggest(@RequestBody AIAutoOrderDTO dto) {
        try {
            Map<String, Object> result = aiService.agentSuggest(dto);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * POST /api/ai/agent-suggest-stream
     * SSE 流式输出：边生成 reply 文本边推送，最后推送完整的 {reply, order}
     */
    @PostMapping(value = "/agent-suggest-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter agentSuggestStream(@RequestBody AIAutoOrderDTO dto) {
        return aiService.agentSuggestStream(dto);
    }
}

