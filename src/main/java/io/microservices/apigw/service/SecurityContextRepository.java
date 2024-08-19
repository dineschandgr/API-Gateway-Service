package io.microservices.apigw.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.microservices.apigw.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

	@Autowired
	private ReactiveAuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtil;

	@Override
	public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange swe) {
		ServerHttpRequest request = swe.getRequest();
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String authToken = authHeader.substring(7);
				Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken, jwtUtil.extractRole(authToken));

				return this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
			} else {
				return Mono.empty();
			}
		} catch (ExpiredJwtException | InsufficientAuthenticationException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

}