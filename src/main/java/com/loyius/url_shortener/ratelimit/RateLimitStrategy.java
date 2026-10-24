package com.loyius.url_shortener.ratelimit;

import java.util.Map;

//pluggable rate limiting algorithm
public interface RateLimitStrategy {

    //Try to consume one request for the given client key
    RateLimitResult tryConsume(String key);

    //current state shown to every known key, WITHOUT consuming anything
    Map<String, RateLimitResult> currentState();

    //name shown to the admin endpoint
    String name();
}
