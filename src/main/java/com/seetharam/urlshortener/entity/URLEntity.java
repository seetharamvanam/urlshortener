package com.seetharam.urlshortener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Table(name = "urlentity", indexes = @Index(name="idx_shorturl", columnList = "shorturl"))
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class URLEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "shorturl", unique = true)
    private String shortURL;
    @Column(name = "original", columnDefinition = "TEXT")
    private String originalURL;
    @Column(name = "createdDate")
    private LocalDateTime createdDate;
    @Column(name = "activestatus")
    private Boolean isActive;
    @Column(name = "updatedDate")
    private LocalDateTime expiresAt;
    @Column(name="hitcount")
    private Long hitCount;
    @ManyToOne
    @JoinColumn(name = "userID")
    private UserEntity userEntity;

    public URLEntity(String originalURL, LocalDateTime createdDate, Boolean isActive){
        this.originalURL = originalURL;
        this.createdDate = createdDate;
        this.isActive = isActive;
    }
    public URLEntity(String shortURL){
        this.shortURL = shortURL;
    }

}
