package sg.ntuchealth.yoda.edge.filter;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import sg.ntuchealth.yoda.edge.service.model.LoginResponse;
import sg.ntuchealth.yoda.edge.service.model.User;
import sg.ntuchealth.yoda.edge.util.SSOTokenUtil;

import java.util.List;

@Component
public class AuthenticationPreFilter implements GlobalFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SSOTokenUtil jwtUtil;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        logger.info("Global Pre Filter executed");

        ServerHttpRequest request = exchange.getRequest();
        List<String> origin = request.getHeaders().get("Origin");
        if (origin != null) {
            origin.forEach(out -> logger.info("origin headers "+out));
        }

        if (this.isAuthMissing(request))
            return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);

        final String token = this.getAuthHeader(request);
        String ssoToken = token.substring(7);
        if (!jwtUtil.isTokenValid(ssoToken))
            return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);

        User user = jwtUtil.retrieveUserFromToken();

        return chain.filter(
                exchange.mutate().request(
                        request.mutate()
                                .header("AssociationId", user.getAssociationID())
                                .build())
                        .build());
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("X-Code",err);
        return response.setComplete();
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

}