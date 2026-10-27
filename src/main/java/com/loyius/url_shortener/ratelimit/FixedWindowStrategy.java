package com.loyius.url_shortener.ratelimit;

import java.time.Clock;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowStrategy implements RateLimitStrategy {
    private final long capacity;
    private final long windowMillis;
    private final Clock clock;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public FixedWindowStrategy(long capacity, Duration window, Clock clock){
        this.capacity = capacity;
        this.windowMillis = window.toMillis();
        this.clock = clock;
    }

    private record WindowCounter(long windowStart, long count) {

        WindowCounter increment() {
            return new WindowCounter(windowStart, count + 1);
        }
    }

    @Override
    public String name(){
        return "fixed-window";
    }

    private long currentWindowStart(long nowMillis){
        return nowMillis - (nowMillis % windowMillis);
    }

    private static long ceilDiv(long value, long divisor){
        return (value + divisor - 1) / divisor;
    }

    @Override
    public Map<String, RateLimitResult> currentState(){
        Map<String, RateLimitResult> state = new LinkedHashMap<>();
        long windowStart = currentWindowStart(clock.millis());
        long resetEpochSeconds = ceilDiv(windowStart + windowMillis, 1000);

        counters.forEach((key, counter) -> {
            long used = counter.windowStart() == windowStart ? counter.count : 0;
            long remaining = Math.max(0, capacity - used);
            state.put(key, new RateLimitResult(remaining > 0, capacity, remaining, resetEpochSeconds, 0));
        });
        return state;
    }

    @Override
    public RateLimitResult tryConsume(String key){
        long now = clock.millis();
        long windowStart = currentWindowStart(now);

        WindowCounter counter = counters.compute(key,
                (k, current) -> current == null || current.windowStart() != windowStart ? new WindowCounter(windowStart, 1) : current.increment()
        );

        long windowEnd = windowStart + windowMillis;
        long resetEpochSeconds = ceilDiv(windowEnd, 1000);
        boolean allowed = counter.count() <= capacity;

        if(allowed){
            return new  RateLimitResult(true, capacity, capacity - counter.count(), resetEpochSeconds, 0);
        }
        long retryAfter = Math.max(1, ceilDiv(windowEnd - now, 1000));
        return new RateLimitResult(false, capacity,0, resetEpochSeconds, retryAfter);
    }
}
