package com.boot.gateway.controller;

import com.boot.gateway.dto.UserDTO;
import com.boot.gateway.model.AuthRequest;
import com.boot.gateway.model.AuthResponse;
import com.boot.gateway.security.JWTUtil;
import com.boot.gateway.security.LoginNotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.boot.gateway.security.JWTUtil.AUTHORIZATION_HEADER;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final WebClient userServiceClient;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @Value("${login.role.name}")
    private String loginRoleName;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        return userServiceClient
                .get()
                .uri("/" + request.getEmail()+"?includePassword=true")
                .retrieve()
                .bodyToMono(UserDTO.class)
                .filter(userDetails -> passwordEncoder.matches(request.getPassword(), userDetails.getPassword()))
                .map(userDetails -> {
                            if (userDetails.getRoles().stream().anyMatch(role -> loginRoleName.equalsIgnoreCase(role))) {
                                return ResponseEntity.ok()
                                        .header(AUTHORIZATION_HEADER, tokenPrefix + jwtUtil.generateToken(userDetails))
                                        .body(new AuthResponse(userDetails.getEmail(), userDetails.getFirstName(), userDetails.getLastName()));
                            }
                            throw new LoginNotAllowedException();
                        }
                )
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}
