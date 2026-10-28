package com.loyius.url_shortener.ratelimit;

import com.loyius.url_shortener.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    public static final String API_KEY_HEADER = "X-API-Key";

    private static final String HEADER_LIMIT = "X-RateLimit-Limit";
    private static final String HEADER_REMAINING = "X-RateLimit-Remaining";
    private static final String HEADER_RESET = "X-RateLimit-Reset";

    private RateLimitStrategy strategy;

    public RateLimitInterceptor(RateLimitStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if(!"POST".equalsIgnoreCase(request.getMethod())){
            return true;
        }
        String apiKey = request.getHeader(API_KEY_HEADER);
        if(apiKey == null || apiKey.isBlank()){
            return true;
        }

        RateLimitResult result = strategy.tryConsume(apiKey);

        response.setHeader(HEADER_RESET,String.valueOf(result.resetEpochSeconds()));

        if(!result.allowed()){
            throw new RateLimitExceededException(result.limit(), result.remaining());
        }

        response.setHeader(HEADER_LIMIT, String.valueOf(result.limit()));
        response.setHeader(HEADER_REMAINING, String.valueOf(result.remaining()));
        return true;
    }
}
