package com.loyius.url_shortener.repository;

import com.loyius.url_shortener.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<ShortUrl,Long> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    boolean existByShortCode(String shortcode);

    List<ShortUrl> findByApiKey(String apiKey);

    //@Query("UPDATE ShortCode s SET s.clickCount = s.clickCount + 1 WHERE s.shortCode = :shortCode")
    @Modifying
    void incrementClickCount(@Param("shortCode") String shortCode);
}
