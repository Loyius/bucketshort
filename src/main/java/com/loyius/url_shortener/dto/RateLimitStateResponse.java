package com.loyius.url_shortener.dto;

import com.loyius.url_shortener.ratelimit.RateLimitResult;

import java.util.Map;

public record RateLimitStateResponse(
        String strategy,
        int trackedKeys,
        Map<String, RateLimitResult> keys
) { }
