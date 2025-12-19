package com.nbufe.zfr.UniTakeout_server.interceptor;

import com.nbufe.zfr.UniTakeout_server.common.BaseContext;
import com.nbufe.zfr.UniTakeout_server.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // 放行委托广场的 GET 请求（列表与详情无需登录）
        if ("GET".equalsIgnoreCase(method) && (uri.startsWith("/api/delegations") || uri.startsWith("/api/shops"))) {
            return true;
        }

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        token = token.substring(7);
        if (!JwtUtil.validateToken(token)) {
            response.setStatus(401);
            return false;
        }

        Long userId = JwtUtil.getUserIdFromToken(token);
        BaseContext.setCurrentUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.removeCurrentUserId();
    }
}

