package com.boot.gateway.security;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex){
        return Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            // How to customize the retured message?
            // Link: https://stackoverflow.com/a/40791087
            String json = String.format("{\"message\": \"%s\"}", ex.getMessage());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            response.getHeaders().setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
            DataBuffer buf = exchange.getResponse().bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            response.writeWith(Mono.just(buf));
        });
    }
}