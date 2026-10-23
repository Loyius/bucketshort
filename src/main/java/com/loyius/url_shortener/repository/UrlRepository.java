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

    /**
     * Used by GET /{code} to resolve the original URL.
     */
    Optional<ShortUrl> findByShortCode(String shortCode);

    /**
     * Used when generating a new short code, to guard against the rare
     * collision case (relevant if the generation strategy ever moves away
     * from a Base62 encoding of the auto-increment id).
     */
    boolean existByShortCode(String shortcode);

    /**
     * Used by GET /admin/limits (or similar admin/reporting endpoints) to
     * inspect what a given client has created.
     */
    List<ShortUrl> findByApiKey(String apiKey);

    /**
     * Atomic increment, avoiding a read-modify-write race between concurrent
     * redirects hitting the same short code at the same time.
     */
    @Modifying
    @Query("UPDATE ShortUrl s SET s.clickCount = s.clickCount + 1 WHERE s.shortCode = :shortCode")
    void incrementClickCount(@Param("shortCode") String shortCode);
}
