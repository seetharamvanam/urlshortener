package com.seetharam.urlshortener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "userentity")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true)
    private String username;
    @Column
    private String hashedPassword;
    @Column(unique = true)
    private String email;
    @Column
    private Boolean active;
    @Column
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "userEntity",  fetch = FetchType.LAZY)
    private List<URLEntity> urls;

    public UserEntity(String userName, String password, String emailAddress, boolean activeStatus, LocalDateTime createdAt) {
        this.username = userName;
        this.hashedPassword = password;
        this.email = emailAddress;
        this.active = activeStatus;
        this.createdAt = createdAt;
    }
}
