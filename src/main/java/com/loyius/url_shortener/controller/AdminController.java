package com.loyius.url_shortener.controller;

import com.loyius.url_shortener.dto.RateLimitStateResponse;
import com.loyius.url_shortener.ratelimit.RateLimitResult;
import com.loyius.url_shortener.ratelimit.RateLimitStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private RateLimitStrategy strategy;

    public AdminController(RateLimitStrategy strategy) {
        this.strategy = strategy;
    }

    @GetMapping("/limits")
    public RateLimitStateResponse limits() {
        Map<String, RateLimitResult> masked = new LinkedHashMap<>(strategy.currentState());
        return new RateLimitStateResponse(strategy.name(), masked.size(), masked);
    }
}
