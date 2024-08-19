package io.microservices.apigw.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.microservices.apigw.service.model.User;
import io.microservices.apigw.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

	@Autowired
	private JwtUtils jwtUtil;

	@Autowired UserService userService;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) throws InsufficientAuthenticationException {


		String authToken = authentication.getCredentials().toString();
		String email = jwtUtil.extractEmail(authToken);

		User userDetails = null;
		try {
			userDetails = userService.getUserDetails(email).map(u -> u).toFuture().get();

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}

		try {
				if (email != null && jwtUtil.isTokenValid(authToken, userDetails)) {
					UsernamePasswordAuthenticationToken auth =
							new UsernamePasswordAuthenticationToken(email, null,
									jwtUtil.extractRole(authToken));
					return Mono.just(auth);
				} else {
					throw new InsufficientAuthenticationException("Could not validate the token");
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}


	}

}