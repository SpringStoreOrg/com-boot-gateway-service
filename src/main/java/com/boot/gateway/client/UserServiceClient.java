package com.boot.gateway.client;

import com.boot.services.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {
	@Autowired
	private RestTemplate userServiceRestTemplate;

	public Mono<User> callGetUserByEmail(String email) {

		return Mono.justOrEmpty(userServiceRestTemplate.getForEntity(Constants.GET_USER_BY_EMAIL , User.class, email).getBody());
	}
}