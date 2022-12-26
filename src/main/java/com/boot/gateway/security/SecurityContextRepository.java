package com.boot.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {
    @Value("${jwt.prefix}")
    private String tokenPrefix;

    private final AuthenticationManager authenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
        return Mono.justOrEmpty(swe.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(tokenPrefix))
                .flatMap(authHeader -> {
                    String authToken = authHeader.substring(tokenPrefix.length());
                    Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
                    return this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
                });
    }
}
