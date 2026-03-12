package com.seetharam.urlshortener.repository;

import com.seetharam.urlshortener.entity.URLEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface URLRepository extends JpaRepository<URLEntity, Long> {
    Optional<URLEntity> findByShortURLAndIsActive(String url, Boolean isActive);
    Optional<URLEntity> findByShortURL(String url);
    Optional<URLEntity> findByOriginalURL(String url);
}
