package com.seetharam.urlshortener.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UrlClickProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    public UrlClickProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendClickEvent(String shortCode){
        kafkaTemplate.send("url-clicks", shortCode);
        System.out.println("sendClickEvent sent to url-clicks with shortCode: " + shortCode);
    }

}
