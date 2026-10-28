package com.loyius.url_shortener.config;

import com.loyius.url_shortener.ratelimit.FixedWindowStrategy;
import com.loyius.url_shortener.ratelimit.RateLimitStrategy;
import com.loyius.url_shortener.ratelimit.TokenBucketStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Picks the active rate limiting strategy from {@code ratelimit.strategy}
 * The rest of the app depends only on the {@link *.url_shortener.ratelimit.RateLimitStrategy} interface,
 * so switching algorithms is a one-line change in application.yml
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitConfig {

    private static final Logger log = LoggerFactory.getLogger(RateLimitConfig.class);

    @Bean
    public Clock clock() { return Clock.systemUTC(); }

    @Bean
    public RateLimitStrategy rateLimitStrategy(RateLimitProperties properties, Clock clock) {
        log.info("Rate limiting: strategy={}, capacity={}, period={}s",
                properties.strategy(), properties.capacity(), properties.refillPeriodSeconds());

        return switch (properties.strategy()) {
            case TOKEN_BUCKET -> new TokenBucketStrategy(properties.capacity(), properties.refillPeriod());
            case FIXED_WINDOW -> new FixedWindowStrategy(properties.capacity(), properties.refillPeriod(), clock);
        };
    }
}
