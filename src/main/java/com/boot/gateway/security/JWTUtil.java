package com.boot.gateway.security;

import java.security.Key;
import java.util.*;

import javax.annotation.PostConstruct;

import com.boot.gateway.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.in.minutes}")
    private String expirationTime;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    private Key key;

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    public String getUserIdFromToken(String token) {
        return getAllClaimsFromToken(token).getId();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDTO user) {
        Map<String, List<String>> claims = new HashMap<>();
        claims.put("role", user.getRoles());
        return doGenerateToken(claims, user.getEmail(), user.getId());
    }

    public boolean hasRole(ServerWebExchange exchange, String role){
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(StringUtils.isNotEmpty(token) && token.startsWith(tokenPrefix)){
            String authToken = token.substring(tokenPrefix.length());

            return hasRole(authToken, role);
        }

        return false;
    }

    public boolean hasRole(String token, String role) {
        return getAllClaimsFromToken(token)
                .get("role", List.class).stream()
                .anyMatch(item -> role.equalsIgnoreCase(String.valueOf(item)));
    }


    private String doGenerateToken(Map<String, List<String>> claims, String username, long userId) {
        Long expirationTimeInMinutes = Long.parseLong(expirationTime); //in minutes
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeInMinutes * 60 * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .setId(String.valueOf(userId))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

}
