package com.boot.gateway.security;

import org.springframework.http.HttpStatus;

public class UserLoginException extends RuntimeException{
    private HttpStatus status;
    public UserLoginException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
