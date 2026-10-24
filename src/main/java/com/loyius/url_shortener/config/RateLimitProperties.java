package com.loyius.url_shortener.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "ratelimit")
public record RateLimitProperties(
        @NotNull
        @DefaultValue("token-bucket")
        StrategyType strategy,

        @Positive
        @DefaultValue("100")
        long capacity,

        @Positive
        @DefaultValue("60")
        long refillPeriodSeconds
) {
    public Duration refillPeriod() {
        return Duration.ofSeconds(this.refillPeriodSeconds);
    }

    public enum StrategyType {
        TOKEN_BUCKET,
        FIXED_WINDOW
    }
}
