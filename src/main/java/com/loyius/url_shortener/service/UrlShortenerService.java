package com.loyius.url_shortener.service;

import com.loyius.url_shortener.dto.ShortenResponse;
import com.loyius.url_shortener.entity.ShortUrl;
import com.loyius.url_shortener.generator.CodeGenerator;
import com.loyius.url_shortener.repository.UrlRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.InvalidUrlException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

@Service
public class UrlShortenerService {

    private static final int MAX_URL_LENGTH = 2048;

    public UrlRepository repository;
    public EntityManager em;
    public CodeGenerator generator;
    public String baseUrl;

    public UrlShortenerService(UrlRepository repository,
                               EntityManager em,
                               CodeGenerator generator,
                               @Value("${app.base-url:http://localhost:8080}")String baseURL) {
        this.repository = repository;
        this.em = em;
        this.generator = generator;
        //learn the one below
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseURL;
    }

    private void validateUrl(String url) {
        //isBlank() or isEmpty()????
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException("url is null or blank");
        }
        if (url.length() > MAX_URL_LENGTH) {
            throw new InvalidUrlException("url mus not exceed " + MAX_URL_LENGTH + " characters");
        }

        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            boolean httpScheme = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
            if (!httpScheme || uri.getHost() == null) {
                throw new InvalidUrlException("URL must be a valid http or https address");
            }
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("Mailformed URL:" + url);
        }
    }

    @Transactional
    public String resolve(String shortCode){
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                //TODO: replace by a custom exception
                .orElseThrow(() ->  new InvalidUrlException("No such shortcode " + shortCode));
        if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
            //TODO: replace by a custom exception
            throw new InvalidUrlException("Expires at is before now");
        }
        repository.incrementClickCount(shortCode);
        return shortUrl.getOriginalUrl();
    }

    @Transactional
    public ShortenResponse shorten(String url, String apiKey) {
        validateUrl(url);

        long id = repository.nextId();
        String code = generator.generate(id);

        ShortUrl entity = ShortUrl.builder()
                .id(id)
                .shortCode(code)
                .originalUrl(url)
                .apiKey(apiKey)
                .build();

        em.persist(entity);

        return new ShortenResponse(
                code,
                baseUrl + "/"+ code,
                entity.getOriginalUrl(),
                entity.getCreatedAt());
    }
}
