package com.boot.gateway.controller;

import com.boot.gateway.security.LoginNotAllowedException;
import com.boot.gateway.security.UserLoginException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-2)
public class RestWebExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono handle(ServerWebExchange exchange, Throwable ex) {

        if (ex instanceof ExpiredJwtException) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        if (ex instanceof LoginNotAllowedException) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        if (ex instanceof UserLoginException) {
            exchange.getResponse().setStatusCode(((UserLoginException) ex).getStatus());
            byte[] bytes = ex.getMessage().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
        return Mono.error(ex);
    }
}
