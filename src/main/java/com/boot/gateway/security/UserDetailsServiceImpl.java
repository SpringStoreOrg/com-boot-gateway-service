package com.boot.gateway.security;


import com.boot.gateway.client.UserServiceClient;
import com.boot.services.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service   // It has to be annotated with @Service.
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

	@Autowired
	private UserServiceClient userServiceClient;

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		User userEntity = userServiceClient.callGetUserByEmail(username).getBody();

		if(userEntity != null) {

			if(!userEntity.isActivated()) {
				throw new UsernameNotFoundException("User using Email: " + username + " not activated!");
			}
			// Remember that Spring needs roles to be in this format: "ROLE_" + userRole (i.e. "ROLE_ADMIN")
			// So, we need to set it to that format, so we can verify and compare roles (i.e. hasRole("ADMIN")).
			List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + userEntity.getRole());

			log.info("User using Email: " + userEntity.getEmail() + " logged in!");
			// The "User" class is provided by Spring and represents a model class for user to be returned by UserDetailsService
			// And used by auth manager to verify and check user authentication.
			return Mono.just(new org.springframework.security.core.userdetails.User(userEntity.getEmail(),userEntity.getPassword(), grantedAuthorities));
		}

		// If user not found. Throw this exception.
		throw new UsernameNotFoundException("Email : " + username + " not found!");
	}
}