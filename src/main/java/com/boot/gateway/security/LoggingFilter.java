package com.boot.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        Instant start = Instant.now();
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    Instant finish = Instant.now();
                    long time = Duration.between(start, finish).toMillis();
                    log.info("{}:{}:{}ms", exchange.getRequest().getPath(), exchange.getResponse().getStatusCode(),time);
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
