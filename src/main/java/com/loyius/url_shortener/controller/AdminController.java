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

    //Creates a mask for the API keys
    private static String mask(String key){
        return key.length() <= 4 ? "****" : key.substring(0, 4) + "****";
    }

    @GetMapping("/limits")
    public RateLimitStateResponse limits() {
        Map<String, RateLimitResult> masked = new LinkedHashMap<>();
        strategy.currentState().forEach((key, state) ->
                masked.put(mask(key), state));
        return new RateLimitStateResponse(strategy.name(), masked.size(), masked);
    }
}
