package com.seetharam.urlshortener.service;

import com.seetharam.urlshortener.config.Base62Encoder;
import com.seetharam.urlshortener.entity.URLEntity;
import com.seetharam.urlshortener.exceptions.InvalidUrlException;
import com.seetharam.urlshortener.exceptions.UrlNotFoundException;
import com.seetharam.urlshortener.repository.URLRepository;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlService {
    private final URLRepository urlRepository;
    public UrlService(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public URLEntity createShortURL(String originalURL) {
        try {
            new URL(originalURL).toURI();
        }catch (URISyntaxException | MalformedURLException e){
            throw new InvalidUrlException("Invalid URL: " + originalURL);
        }
        URLEntity newUrlEntity = new URLEntity();
        newUrlEntity.setOriginalURL(originalURL);
        newUrlEntity.setCreatedDate(LocalDateTime.now());
        newUrlEntity.setIsActive(true);
        newUrlEntity.setHitCount(0L);
        newUrlEntity.setExpiresAt(LocalDateTime.now().plusYears(1));
        URLEntity savedUrlEntity = urlRepository.save(newUrlEntity);
        String shortURL = Base62Encoder.encode(savedUrlEntity.getId());
        savedUrlEntity.setShortURL(shortURL);
        urlRepository.save(savedUrlEntity);
        return savedUrlEntity;
    }

    public String getOriginalURL(String shortURL){
        if(shortURL == null || shortURL.isEmpty()){
            throw new InvalidUrlException("Invalid URL: " + shortURL);
        }
        Optional<URLEntity> urlEntity = urlRepository.findByShortURLAndIsActive(shortURL,true);
        if(urlEntity.isEmpty()){
            throw new UrlNotFoundException("URL not found: " + shortURL);
        }

            return urlEntity.get().getOriginalURL();
    }
}
