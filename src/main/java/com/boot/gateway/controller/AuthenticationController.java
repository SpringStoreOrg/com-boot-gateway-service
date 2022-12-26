package com.boot.gateway.controller;

import com.boot.gateway.client.UserServiceClient;
import com.boot.gateway.model.AuthRequest;
import com.boot.gateway.model.AuthResponse;
import com.boot.gateway.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        return userServiceClient.callGetUserByEmail(request.getEmail())
                .filter(userDetails -> passwordEncoder.matches(request.getPassword(), userDetails.getPassword()))
                .map(userDetails ->
                        ResponseEntity.ok()
                                .header("Authorization", tokenPrefix + jwtUtil.generateToken(userDetails))
                                .body(new AuthResponse(userDetails.getEmail()))
                )
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}
