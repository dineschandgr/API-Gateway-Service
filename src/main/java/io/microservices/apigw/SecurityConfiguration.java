package io.microservices.apigw;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
			ReactiveAuthenticationManager authenticationManager,
			ServerSecurityContextRepository securityContextRepository) {

		http = http.headers(headers -> headers.contentSecurityPolicy(
				contentSecurityPolicy -> contentSecurityPolicy.policyDirectives("script-src 'self'")));

		return http.csrf().disable().formLogin().disable().httpBasic().disable()
				.authenticationManager(authenticationManager).securityContextRepository(securityContextRepository)
				.authorizeExchange().pathMatchers(HttpMethod.OPTIONS).permitAll()
				.pathMatchers("/api/**").permitAll()
				.pathMatchers("/user/**").permitAll()
				.pathMatchers("/address/**").hasAnyAuthority("SELLER","CUSTOMER")
				.pathMatchers("/category/**").hasAuthority("ADMIN")
				.pathMatchers("/product/**").hasAuthority("SELLER")
				.pathMatchers("/cart/**").hasAuthority("CUSTOMER")
				.pathMatchers("/order/**").hasAuthority("CUSTOMER")
				.pathMatchers("/payment/**").hasAuthority("CUSTOMER")
//				.pathMatchers("/home/**").hasAuthority("CUSTOMER")
//				.pathMatchers("/api/user/**").permitAll()
//				.pathMatchers( "/category/**").hasAuthority("ADMIN")
//				.pathMatchers("/product/**").hasAuthority("SELLER")
//				.pathMatchers("/address/**").hasAnyAuthority("SELLER","CUSTOMER")
//				.pathMatchers("/cart/**","/order/**","/payment/**").hasAuthority("CUSTOMER")
//				.pathMatchers("/swagger-ui/**").authenticated()
//				.pathMatchers("/v3/**").permitAll()
//				.pathMatchers("/checkout").hasAuthority("CUSTOMER")
//				.pathMatchers("/payment").hasAuthority("CUSTOMER")
				.anyExchange().authenticated().and().build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


}
