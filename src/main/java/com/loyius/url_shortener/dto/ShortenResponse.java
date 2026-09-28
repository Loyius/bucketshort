package com.loyius.url_shortener.dto;

import java.time.Instant;

public record ShortenResponse(
        String shortCode,
        String shortUrl,
        String  originalUrl,
        Instant createdAt
) { }
