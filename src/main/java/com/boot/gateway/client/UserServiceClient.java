package com.boot.gateway.client;

import com.boot.services.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceClient {

	@Autowired
	private RestTemplate userServiceRestTemplate;

	public ResponseEntity<User> callGetUserByEmail(String email) {

		return userServiceRestTemplate.getForEntity(Constants.GET_USER_BY_EMAIL , User.class, email);
	}
}