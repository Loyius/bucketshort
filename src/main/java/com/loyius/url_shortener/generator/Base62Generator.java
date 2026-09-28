package com.loyius.url_shortener.generator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.code-generator", havingValue = "base62", matchIfMissing = true)
public class Base62Generator implements CodeGenerator {

    private static final String ALPHABET = "0123456789abcdefghijkmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int BASE =  ALPHABET.length();

    @Override
    public String generate(long id) {
        if(id < 0){
            throw new IllegalArgumentException("Id must be non-negative: " + id);
        }
        if(id == 0){
            return String.valueOf(ALPHABET.charAt(0));
        }

        StringBuilder sb = new StringBuilder(ALPHABET);
        while(id > 0){
            sb.append(ALPHABET.charAt((int) (id%BASE)));
            id/=BASE;
        }
        return sb.reverse().toString();
    }
}
