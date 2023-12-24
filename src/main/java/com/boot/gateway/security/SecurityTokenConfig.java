package com.boot.gateway.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;


@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityTokenConfig {
	private AuthenticationManager authenticationManager;
	private SecurityContextRepository securityContextRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
				.exceptionHandling()
				.authenticationEntryPoint((swe, e) ->
						Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
				).accessDeniedHandler((swe, e) ->
						Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
				).and()
				.csrf().disable()
				.formLogin().disable()
				.httpBasic().disable()
				.authenticationManager(authenticationManager)
				.securityContextRepository(securityContextRepository)
				.authorizeExchange()
				.pathMatchers(HttpMethod.OPTIONS).permitAll()
				.pathMatchers(HttpMethod.POST, "/login", "/user/", "/user/*", "/user/customerMessage", "/order").permitAll()
				.pathMatchers(HttpMethod.GET, "/user/confirm/*", "/product", "/product/*", "/order/*").permitAll()
				.pathMatchers(HttpMethod.PUT, "/user/confirm/*", "/user/password/reset/*", "/user/password/change").permitAll()
				.anyExchange().authenticated()
				.and().build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedOriginPattern("*");
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
		corsConfiguration.addAllowedHeader("origin");
		corsConfiguration.addAllowedHeader("content-type");
		corsConfiguration.addAllowedHeader("accept");
		corsConfiguration.addAllowedHeader("authorization");
		corsConfiguration.addAllowedHeader("cookie");
		corsConfiguration.setExposedHeaders(Arrays.asList("authorization"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}
}