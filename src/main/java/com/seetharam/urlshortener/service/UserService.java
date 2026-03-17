package com.seetharam.urlshortener.service;

import com.seetharam.urlshortener.config.JwtUtil;
import com.seetharam.urlshortener.dto.LoginUserRequest;
import com.seetharam.urlshortener.dto.RegisterUserRequest;
import com.seetharam.urlshortener.dto.RegisterUserResponse;
import com.seetharam.urlshortener.entity.UserEntity;
import com.seetharam.urlshortener.exceptions.InvalidPasswordException;
import com.seetharam.urlshortener.exceptions.InvalidRequestException;
import com.seetharam.urlshortener.exceptions.InvalidUrlException;
import com.seetharam.urlshortener.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
        if (registerUserRequest.userEmail() == null || registerUserRequest.userName() == null || registerUserRequest.password() == null) {
            throw new InvalidRequestException("All fields are required");
        }
        if (userRepository.findByUsername(registerUserRequest.userName()).isPresent()) {
            throw new InvalidRequestException("Username already exists");
        }
        if (userRepository.findByEmail(registerUserRequest.userEmail()).isPresent()) {
            throw new InvalidRequestException("UserEmail already exists");
        }
        UserEntity newUser = new UserEntity(registerUserRequest.userName(), bCryptPasswordEncoder.encode(registerUserRequest.password()),
                registerUserRequest.userEmail(), true, LocalDateTime.now());
        userRepository.save(newUser);
        return new RegisterUserResponse(newUser.getEmail(), "Account Created");
    }

    public String loginUser(LoginUserRequest loginUserRequest) {
        if (loginUserRequest.email() == null || loginUserRequest.password() == null) {
            throw new InvalidRequestException("All fields are required");
        }
        Optional<UserEntity> optionalUser = userRepository.findByEmail(loginUserRequest.email());
        if (!optionalUser.isPresent()) {
            throw new InvalidRequestException("User not found");
        }
        UserEntity user = optionalUser.get();
        if (!bCryptPasswordEncoder.matches(loginUserRequest.password(), user.getHashedPassword())) {
            throw new InvalidPasswordException("Invalid Password");
        }
        return jwtUtil.generateToken(user.getEmail());
    }
}
