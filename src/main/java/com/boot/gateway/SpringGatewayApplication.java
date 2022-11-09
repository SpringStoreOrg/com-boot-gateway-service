package com.boot.gateway;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableEurekaClient    // It acts as a eureka client
@EnableDiscoveryClient
@EnableWebFlux
public class SpringGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGatewayApplication.class, args);
	}

	@Value("${user.service.url}")
	public String userServiceUrl;

	@Bean(name="userServiceRestTemplate")
	public RestTemplate userServiceRestTemplateUrl() {
		return new RestTemplateBuilder().rootUri(userServiceUrl).build();
	}
}