package com.boot.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@SpringBootApplication
//@EnableEurekaClient 	// It acts as a eureka client
@EnableDiscoveryClient
public class SpringGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGatewayApplication.class, args);
	}
	
	@Bean
	public CorsFilter corsFilter() {
	    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    final CorsConfiguration config = new CorsConfiguration();
	    config.setAllowCredentials(true);
	    config.setAllowedOriginPatterns(Collections.singletonList("*"));
	    config.setAllowedHeaders(Collections.singletonList("*"));
	    config.setAllowedMethods(Arrays.stream(HttpMethod.values()).map(HttpMethod::name).collect(Collectors.toList()));
	    config.setExposedHeaders(Arrays.asList("Authorization"));
	    source.registerCorsConfiguration("/**", config);
	    return new CorsFilter(source);
	}

//	@Bean
//	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route(r -> r.path("/product/**")
//						.uri("http://PRODUCT-SERVICE:8081/"))
//
//				.route(r -> r.path("/cart/**")
//						.uri("http://CART-SERVICE:8083/"))
//				.build();
//	}
//
//	@Bean
//	public RouteLocatorBuilder routeLocatorBuilder(ConfigurableApplicationContext context){
//		return new RouteLocatorBuilder(context);
//	}
//
//	@Bean
//	public PathRoutePredicateFactory pathRoutePredicateFactory(){
//		return new PathRoutePredicateFactory();
//	}
}