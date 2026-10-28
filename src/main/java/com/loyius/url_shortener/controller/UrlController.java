package com.loyius.url_shortener.controller;

import com.loyius.url_shortener.dto.ShortenRequest;
import com.loyius.url_shortener.dto.ShortenResponse;
import com.loyius.url_shortener.ratelimit.RateLimitInterceptor;
import com.loyius.url_shortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class UrlController {
    private final UrlShortenerService service;

    public UrlController(UrlShortenerService service) { this.service = service; }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(
            @RequestHeader(RateLimitInterceptor.API_KEY_HEADER) String apiKey,
            @Valid @RequestBody ShortenRequest request){
        ShortenResponse response = service.shorten(request.url(), apiKey);

        return ResponseEntity.created(URI.create(response.shortUrl()))
                .body(response);
    }

    @GetMapping("/{code:[0-9a-zA-Z]{1,16}}")
    public ResponseEntity<Void> redirect(@PathVariable String code){
        String originalUrl = service.resolve(code);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
