package com.loyius.url_shortener.config;

import com.loyius.url_shortener.ratelimit.RateLimitInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class Webconfig implements WebMvcConfigurer {
    private final RateLimitInterceptor interceptor;

    public Webconfig(RateLimitInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
        .addPathPatterns("/shorten");
    }
}
