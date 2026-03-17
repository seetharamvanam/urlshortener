package com.seetharam.urlshortener.controller;

import com.seetharam.urlshortener.dto.Urlrequest;
import com.seetharam.urlshortener.entity.URLEntity;
import com.seetharam.urlshortener.service.UrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlService urlService;
    public UrlController(UrlService urlService){
        this.urlService = urlService;
    }

    @PostMapping("/urls")
    public ResponseEntity<URLEntity> createShortURL(@RequestBody Urlrequest longUrl){
        return new ResponseEntity(urlService.createShortURL(longUrl.longUrl()), HttpStatus.CREATED);
    }

    @GetMapping("/{shortURL}")
    public ResponseEntity<Void> getOriginalUrl(@PathVariable String shortURL){
        String  originalUrl = urlService.getOriginalURL(shortURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PutMapping("/{shortURL}")
    public ResponseEntity<?> deactivateShortURL(@PathVariable String shortURL){
        urlService.deactivateShortURL(shortURL);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
