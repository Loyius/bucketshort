# Rate-Limited URL Shortener

A URL shortener API built to explore and demonstrate **rate limiting strategies** in Java, using [Bucket4j](https://github.com/bucket4j/bucket4j) and Spring Boot.

This is the **first project in a 3-part series** exploring rate limiting and throttling in Java, progressing from a basic implementation to a distributed, Redis-backed gateway:

1. **`rate-limited-url-shortener`** (aka: this repo) pure rate limiting, pluggable strategies

## Why a URL shortener?

The domain is intentionally simple, the goal of this project isn't the business logic, it's the rate limiting layer. Keeping the domain trivial makes it easy to see exactly how the rate limiting works without business logic getting in the way...