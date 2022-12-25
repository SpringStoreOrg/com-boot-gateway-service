package com.boot.gateway.controller;

import com.boot.gateway.client.UserServiceClient;
import com.boot.gateway.model.AuthRequest;
import com.boot.gateway.model.AuthResponse;
import com.boot.gateway.security.JWTUtil;
import com.boot.gateway.security.PBKDF2Encoder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class AuthenticationController {

    private JWTUtil jwtUtil;
    private PBKDF2Encoder passwordEncoder;
    private UserServiceClient userServiceClient;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest ar) {
        return userServiceClient.callGetUserByEmail(ar.getEmail())
            //.filter(userDetails -> passwordEncoder.encode(ar.getPassword()).equals(userDetails.getPassword()))
            .map(userDetails ->
                    ResponseEntity.ok()
                            .header("Authorization", "Bearer "+jwtUtil.generateToken(userDetails))
                            .body(new AuthResponse(jwtUtil.generateToken(userDetails)))
            )
            .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

}
