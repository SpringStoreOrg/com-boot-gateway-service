package com.boot.gateway.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class AddAuthenticationHeadersGlobalPreFilter implements GlobalFilter {
    @Value("${jwt.prefix}")
    private String tokenPrefix;

    private final JWTUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        ServerWebExchange mutatedExchange = exchange;
        if(StringUtils.isNotEmpty(token) && token.startsWith(tokenPrefix)){
            String authToken = token.substring(tokenPrefix.length());
            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header("User-Id", jwtUtil.getUserIdFromToken(authToken))
                    .header("Username", jwtUtil.getUsernameFromToken(authToken))
                    .build();

            mutatedExchange = exchange.mutate().request(request).build();
        }

        return chain.filter(mutatedExchange);
    }
}
