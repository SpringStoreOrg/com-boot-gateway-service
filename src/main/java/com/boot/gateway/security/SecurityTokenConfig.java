package com.boot.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@EnableWebFluxSecurity // Enable security config. This annotation denotes config for spring security.
@Configuration
public class SecurityTokenConfig {
    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private JwtTokenAuthenticationWebFilter authenticationWebFilter;

	@Autowired
    private UserDetailsServiceImpl userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(11);
	}

	@Bean
	public ServerSecurityContextRepository securityContextRepository() {
		WebSessionServerSecurityContextRepository securityContextRepository =
				new WebSessionServerSecurityContextRepository();

		securityContextRepository.setSpringSecurityContextAttrName("securityContext");

		return securityContextRepository;
	}

	@Bean
	public ReactiveAuthenticationManager authenticationManager() {
		UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
				new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);

		authenticationManager.setPasswordEncoder(passwordEncoder());

		return authenticationManager;
	}

	@Bean
	public SecurityWebFilterChain securityWebFiltersOrder(ServerHttpSecurity httpSecurity,
														  ReactiveAuthenticationManager authenticationManager) {
		return httpSecurity
				.cors().disable()
				.csrf().disable()
				.httpBasic().disable()
				.logout().disable()
				.formLogin().disable()
				.securityContextRepository(securityContextRepository())
				.authenticationManager(authenticationManager)
				.authorizeExchange()
				.pathMatchers(HttpMethod.POST, jwtConfig.getUri(), "/user/", "/user/*").permitAll()
				.pathMatchers(HttpMethod.OPTIONS, jwtConfig.getUri(), "/user/", "/user/*").permitAll()
				.pathMatchers(HttpMethod.GET,"/user/confirm/*", "/product", "/product/*").permitAll()
				.pathMatchers(HttpMethod.PUT,"/user/confirm/*").permitAll()
				.pathMatchers(HttpMethod.GET,"/actuator", "/actuator/*").permitAll()
				.and()
				.addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}

	@Bean
	public CorsWebFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedOriginPattern("*");
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
		corsConfiguration.addAllowedHeader("origin");
		corsConfiguration.addAllowedHeader("content-type");
		corsConfiguration.addAllowedHeader("accept");
		corsConfiguration.addAllowedHeader("authorization");
		corsConfiguration.addAllowedHeader("cookie");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsWebFilter(source);
	}
}