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

    @Value("${rate.limit.requests}")
    private int maxRequests;

    @Value("${rate.limit.duration}")
    private long duration;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String ip = request.getRemoteAddr();
        String key = "rate_limit:" + ip;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, duration, TimeUnit.SECONDS);
        }

        if (count > maxRequests) {
            response.setStatus(429);
            response.getWriter().write("Too many requests. Try again later.");
            return false;
        }

        return true;
    }
}
