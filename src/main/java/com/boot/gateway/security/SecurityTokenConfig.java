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


@EnableWebFluxSecurity // Enable security config. This annotation denotes config for spring security.
@Configuration
public class SecurityTokenConfig {
    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private JwtTokenAuthenticationWebFilter authenticationWebFilter;

	@Autowired
    private UserDetailsServiceImpl userDetailsService;
/*
    @Bean
    public SecurityWebFilterChain webHttpSecurity(ServerHttpSecurity http) {
		http.cors().and().csrf().disable()
				// make sure we use stateless session; session won't be used to store user's
				// state.

				//.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				// handle an authorized attempts
				//.exceptionHandling()
				//.authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)).and()
				// Add a filter to validate the tokens with every request
				.addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
				// authorization requests config
				.authorizeRequests()
				// allow all who are accessing "auth" service
				//.antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()
				//.antMatchers(HttpMethod.POST,"/user/*").permitAll()
				//.antMatchers(HttpMethod.GET,"/user/confirm/*").permitAll()
				//.antMatchers(HttpMethod.PUT,"/user/confirm/*").permitAll()
				//.antMatchers(HttpMethod.GET,"/product/*").permitAll()
				//.antMatchers(HttpMethod.GET,"/actuator/*").permitAll()

				// Any other request must be authenticated
				.anyRequest().authenticated();
        return http.build();
    }

*/

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
				.pathMatchers(HttpMethod.POST, jwtConfig.getUri(), "/user/*").permitAll()
				.pathMatchers(HttpMethod.GET,"/user/confirm/*", "/product", "/product/*").permitAll()
				.pathMatchers(HttpMethod.PUT,"/user/confirm/*").permitAll()
				.pathMatchers(HttpMethod.GET,"/actuator", "/actuator/*").permitAll()
				.and()
				.addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}
}