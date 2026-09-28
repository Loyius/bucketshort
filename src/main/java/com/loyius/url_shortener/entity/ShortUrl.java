package com.loyius.url_shortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "short_url")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShortUrl {

    @Id
    private Long id;

    @Column(name = "short_code",nullable = false, unique = true, length = 50)
    private String shortCode;

    @Column(name = "original_url",nullable = false, length = 50)
    private String originalURL;

    @Column(name = "api_key", nullable = false, length = 2048)
    private String apiKey;

    @Column(name = "click_count", nullable = false)
    @Builder.Default
    private Long clickCount = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @PrePersist
    void onCreate(){
        this.createdAt = Instant.now();
    }
}
