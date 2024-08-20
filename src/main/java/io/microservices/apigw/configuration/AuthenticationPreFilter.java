package io.microservices.apigw.configuration;

import io.microservices.apigw.util.JwtUtils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationPreFilter implements GlobalFilter {

  public static final String AUTHORIZATION = "Authorization";
  public static final String VIEW_AS = "view-as";
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private JwtUtils jwtUtil;

  @SneakyThrows
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    LOGGER.info("Global Pre Filter executed");

    ServerHttpRequest request = exchange.getRequest();
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String authToken = authHeader.substring(7);
      Long userId = jwtUtil.extractUserId(authToken);

      return chain.filter(
              exchange
                      .mutate()
                      .request(
                              request
                                      .mutate()
                                      .header("userId", userId.toString())
                                      .build())
                      .build());
    }

      return chain.filter(
              exchange
                      .mutate()
                      .request(
                              request
                                      .mutate()
                                      .build())
                      .build());
    }
  }
