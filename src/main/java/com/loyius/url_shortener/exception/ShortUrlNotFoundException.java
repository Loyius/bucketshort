package com.loyius.url_shortener.exception;

public class ShortUrlNotFoundException extends RuntimeException{

    private final String shortCode;

    public ShortUrlNotFoundException(String shortCode) {
        super("Short code " + shortCode + " not found");
        this.shortCode = shortCode;
    }

    public String getShortCode() { return shortCode; }
}
