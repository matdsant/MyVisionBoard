package com.myvisionboard.app.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitConfig implements HandlerInterceptor {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${rate.limit.auth.requests:10}")
    private int authMaxRequests;

    @Value("${rate.limit.auth.duration:60}")
    private long authDuration;

    @Value("${rate.limit.api.requests:100}")
    private int apiMaxRequests;

    @Value("${rate.limit.api.duration:60}")
    private long apiDuration;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();

        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            return true;
        }

        if (path.startsWith("/auth")) {
            return checkRateLimit(request, response,
                    "rate_limit:ip:" + request.getRemoteAddr(),
                    authMaxRequests, authDuration);
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = extractEmailFromToken(token);
            if (email != null) {
                return checkRateLimit(request, response,
                        "rate_limit:user:" + email,
                        apiMaxRequests, apiDuration);
            }
        }

        return checkRateLimit(request, response,
                "rate_limit:ip:" + request.getRemoteAddr(),
                apiMaxRequests, apiDuration);
    }

    private boolean checkRateLimit(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String key,
                                   int maxRequests,
                                   long duration) throws Exception {
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, duration, TimeUnit.SECONDS);
        }

        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, maxRequests - count)));

        if (count > maxRequests) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Try again later.\"}");
            return false;
        }

        return true;
    }

    private String extractEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readTree(payload).get("sub").asText();
        } catch (Exception e) {
            return null;
        }
    }
}
