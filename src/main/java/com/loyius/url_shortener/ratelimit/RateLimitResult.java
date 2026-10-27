package com.loyius.url_shortener.ratelimit;

/**
 * @param allowed whether the request may proceed
 * @param limit max requests per period
 * @param remaining requests left in the current period
 * @param resetEpochSeconds when the limit fully resets (Unix epoch seconds)
 * @param retryAfterSeconds when the client may try again (0 if allowed)
 */
public record RateLimitResult(
        boolean allowed,
        long limit,
        long remaining,
        long resetEpochSeconds,
        long retryAfterSeconds
) {
}
