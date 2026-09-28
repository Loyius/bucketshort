package com.loyius.url_shortener.repository;

import com.loyius.url_shortener.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<ShortUrl,Long> {

    @Query(value = "SELECT nextval('short_url_id_seq')", nativeQuery = true)
    Long nextId();

    Optional<ShortUrl> findByShortCode(String shortCode);

    boolean existByShortCode(String shortcode);

    List<ShortUrl> findByApiKey(String apiKey);

    @Modifying
    @Query("UPDATE ShortUrl s SET s.clickCount = s.clickCount + 1 WHERE s.shortCode = :shortCode")
    void incrementClickCount(@Param("shortCode") String shortCode);
}
