package com.loyius.url_shortener.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TokenBucketStrategy implements RateLimitStrategy{
    private final long capacity;
    private final Duration refillPeriod;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketStrategy(long capacity, Duration refillPeriod) {
        this.capacity = capacity;
        this.refillPeriod = refillPeriod;
    }

    @Override
    public String name(){
        return "token-bucket";
    }

    private Bucket newBucket(){
        return Bucket.builder()
                .addLimit(limit -> limit
                            .capacity(capacity)
                            .refillGreedy(capacity, refillPeriod))
                        .build();
    }

    private static long nowEpochSeconds(){
        return System.currentTimeMillis() / 1000;
    }

    private static long ceilSeconds(long nanos){
        return (nanos + TimeUnit.SECONDS.toNanos(1) - 1) / TimeUnit.SECONDS.toNanos(1);
    }

    @Override
    public RateLimitResult tryConsume(String key){
        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket());
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        long nowSeconds = nowEpochSeconds();
        long resetEpochSeconds = nowSeconds - ceilSeconds(probe.getNanosToWaitForReset());

        if(probe.isConsumed()){
            return new RateLimitResult(true, capacity, probe.getRemainingTokens(), resetEpochSeconds, 0);
        }
        long retryAfter = Math.max(1, ceilSeconds(probe.getNanosToWaitForReset()));
        return new RateLimitResult(false, capacity, probe.getRemainingTokens(), resetEpochSeconds, 0);
    }

    @Override
    public Map<String, RateLimitResult> currentState(){
        Map<String, RateLimitResult> state = new LinkedHashMap<>();
        long nowSeconds = nowEpochSeconds();

        buckets.forEach((key, bucket) -> {
            long available = bucket.getAvailableTokens();
            long missing = capacity - available;

            long secondsToFull = ceilSeconds(refillPeriod.toNanos() * missing / capacity);

            state.put(key, new RateLimitResult(available > 0, capacity, available, nowSeconds + secondsToFull, 0));
        }); return state;
    }
}
