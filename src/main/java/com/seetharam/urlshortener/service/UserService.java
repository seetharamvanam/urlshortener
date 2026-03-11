package com.seetharam.urlshortener.service;

import com.seetharam.urlshortener.dto.RegisterUserRequest;
import com.seetharam.urlshortener.dto.RegisterUserResponse;
import com.seetharam.urlshortener.entity.UserEntity;
import com.seetharam.urlshortener.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserService {

    private final UserRepository userRepository;

    public UserService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<RegisterUserResponse> registerUser(RegisterUserRequest registerUserRequest) {
        if(registerUserRequest.userEmail() == null || registerUserRequest.userName() == null || registerUserRequest.password() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserEntity newUser = new UserEntity(registerUserRequest.userName(), registerUserRequest.password(),
                registerUserRequest.userEmail(), true, LocalDateTime.now());
        userRepository.save(newUser);
        return new ResponseEntity(new RegisterUserResponse(newUser.getEmail(), "Account Created"), HttpStatus.CREATED);
    }

}
