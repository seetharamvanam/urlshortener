package com.seetharam.urlshortener.controller;

import com.seetharam.urlshortener.dto.LoginUserRequest;
import com.seetharam.urlshortener.dto.RegisterUserRequest;
import com.seetharam.urlshortener.dto.RegisterUserResponse;
import com.seetharam.urlshortener.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(@RequestBody RegisterUserRequest registerUserRequest){
        return  new ResponseEntity<>(userService.registerUser(registerUserRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody LoginUserRequest loginUserRequest){
        return userService.loginUser(loginUserRequest);
    }
}
