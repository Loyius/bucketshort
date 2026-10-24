package com.loyius.url_shortener.exception;

public class RateLimitExceededException extends RuntimeException {
    private final long limit;
    private final long retryAfterSeconds;

    public RateLimitExceededException(long limit, long retryAfterSeconds) {
        super("Rate limit exceeded. Try again in " + retryAfterSeconds + " seconds");
        this.limit = limit;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getLimit() { return limit; }

    public long getRetryAfterSeconds() { return retryAfterSeconds; }
}
