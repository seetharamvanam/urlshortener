package com.seetharam.urlshortener.dto;

public record RegisterUserRequest(String userEmail, String password, String userName) {
}
