package com.seetharam.urlshortener.kafka;

import com.seetharam.urlshortener.entity.URLEntity;
import com.seetharam.urlshortener.repository.URLRepository;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlClickConsumer {

    private final URLRepository urlRepository;
    public UrlClickConsumer(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @KafkaListener(topics = "url-clicks",
            groupId = "url-shortener-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void handleUrlClick(String shortCode){
        System.out.println("=== CONSUMER RECEIVED: " + shortCode + " ===");
        Optional<URLEntity> entity = urlRepository.findByShortURLAndIsActive(shortCode, true);
        System.out.println("=== ENTITY FOUND: " + entity.isPresent() + " ===");
        if(entity.isPresent()){
            entity.get().setHitCount(entity.get().getHitCount()+1);
            urlRepository.save(entity.get());
            System.out.println("=== HIT COUNT UPDATED ===");
        }
    }

}
