package com.boot.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenAuthenticationWebFilter implements WebFilter {
    
	private final JwtConfig jwtConfig;
	
	public JwtTokenAuthenticationWebFilter(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		// 1. get the authentication header. Tokens are supposed to be passed in the authentication header
		String header = exchange.getRequest().getHeaders().getFirst(jwtConfig.getHeader());

		// 2. validate the header and check the prefix
		if(header == null || !header.startsWith(jwtConfig.getPrefix())) {
			return chain.filter(exchange);  		// If not valid, go to the next filter.
		}

		// If there is no token provided and hence the user won't be authenticated.
		// It's Ok. Maybe the user accessing a public path or asking for a token.

		// All secured paths that needs a token are already defined and secured in config class.
		// And If user tried to access without access token, then he won't be authenticated and an exception will be thrown.

		// 3. Get the token
		String token = header.replace(jwtConfig.getPrefix(), "");

		try {	// exceptions might be thrown in creating the claims if for example the token is expired

			// 4. Validate the token
			Claims claims = Jwts.parser()
					.setSigningKey(Base64.getEncoder().encodeToString("encoded-key".getBytes(StandardCharsets.US_ASCII)))
					.parseClaimsJws(token)
					.getBody();

			String username = claims.getSubject();
			if(username != null) {
				@SuppressWarnings("unchecked")
				List<String> authorities = (List<String>) claims.get("authorities");

				// 5. Create auth object
				// UsernamePasswordAuthenticationToken: A built-in object, used by spring to represent the current authenticated / being authenticated user.
				// It needs a list of authorities, which has type of GrantedAuthority interface, where SimpleGrantedAuthority is an implementation of that interface
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

				// 6. Authenticate the user
				// Now, user is authenticated
				SecurityContextHolder.getContext().setAuthentication(auth);
			}

		} catch (Exception e) {
			// In case of failure. Make sure it's clear; so guarantee user won't be authenticated
			SecurityContextHolder.clearContext();
		}

		// go to the next filter in the filter chain
		return chain.filter(exchange);
	}
}