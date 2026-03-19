package com.seetharam.urlshortener.service;

import com.seetharam.urlshortener.config.Base62Encoder;
import com.seetharam.urlshortener.entity.URLEntity;
import com.seetharam.urlshortener.entity.UserEntity;
import com.seetharam.urlshortener.exceptions.InvalidRequestException;
import com.seetharam.urlshortener.exceptions.InvalidUrlException;
import com.seetharam.urlshortener.exceptions.UnAuthorizedException;
import com.seetharam.urlshortener.exceptions.UrlNotFoundException;
import com.seetharam.urlshortener.kafka.UrlClickProducer;
import com.seetharam.urlshortener.repository.URLRepository;
import com.seetharam.urlshortener.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {
    private final URLRepository urlRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final UrlClickProducer urlClickProducer;

    public UrlService(URLRepository urlRepository, UserRepository userRepository, RedisTemplate<String, String> redisTemplate, UrlClickProducer urlClickProducer) {
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.urlClickProducer = urlClickProducer;
    }

    public URLEntity createShortURL(String originalURL) {
        try {
            new URL(originalURL).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new InvalidUrlException("Invalid URL: " + originalURL);
        }
        Optional<UserEntity> user = userRepository.findByEmail(getCurrentUserEmail());
        if (user.isEmpty()) {
            throw new InvalidRequestException("User not found");
        }
        URLEntity newUrlEntity = new URLEntity();
        newUrlEntity.setOriginalURL(originalURL);
        newUrlEntity.setCreatedDate(LocalDateTime.now());
        newUrlEntity.setIsActive(true);
        newUrlEntity.setHitCount(0L);
        newUrlEntity.setExpiresAt(LocalDateTime.now().plusYears(1));
        newUrlEntity.setUserEntity(user.get());
        URLEntity savedUrlEntity = urlRepository.save(newUrlEntity);
        String shortURL = Base62Encoder.encode(savedUrlEntity.getId());
        savedUrlEntity.setShortURL(shortURL);
        urlRepository.save(savedUrlEntity);
        return savedUrlEntity;
    }

    public String getOriginalURL(String shortURL) {
        if (shortURL == null || shortURL.isEmpty()) {
            throw new InvalidUrlException("Invalid URL: " + shortURL);
        }
        String cachedUrl = redisTemplate.opsForValue().get(shortURL);
        String originalUrl;
        if (cachedUrl != null) {
           originalUrl = cachedUrl;
        } else {
            Optional<URLEntity> urlEntity = urlRepository.findByShortURLAndIsActive(shortURL, true);
            if (urlEntity.isEmpty()) {
                throw new UrlNotFoundException("URL not found: " + shortURL);
            }
            redisTemplate.opsForValue().set(shortURL, urlEntity.get().getOriginalURL(), 24, TimeUnit.HOURS);
            originalUrl = urlEntity.get().getOriginalURL();
        }
        urlClickProducer.sendClickEvent(shortURL);
        return originalUrl;
    }

    public void deactivateShortURL(String shortURL) {
        if (shortURL == null || shortURL.isEmpty()) {
            throw new InvalidUrlException("Invalid URL: " + shortURL);
        }
        Optional<UserEntity> user = userRepository.findByEmail(getCurrentUserEmail());
        if (user.isEmpty()) {
            throw new InvalidRequestException("User not found");
        }
        Optional<URLEntity> urlEntity = urlRepository.findByShortURLAndIsActive(shortURL, true);
        if (urlEntity.isEmpty()) {
            throw new UrlNotFoundException("URL not found: " + shortURL);
        }
        if (urlEntity.get().getUserEntity().getEmail().equals(getCurrentUserEmail())) {
            urlEntity.get().setIsActive(false);
            urlRepository.save(urlEntity.get());
            redisTemplate.delete(shortURL);
        } else {
            throw new UnAuthorizedException("You are not authorized to deactivate this URL");
        }
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
