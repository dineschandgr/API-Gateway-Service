package sg.ntuchealth.yoda.edge.filter;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import sg.ntuchealth.yoda.edge.filter.exceptions.AssocationNotFoundGlobalException;
import sg.ntuchealth.yoda.edge.filter.exceptions.AuthorizationGlobalException;
import sg.ntuchealth.yoda.edge.service.UserService;
import sg.ntuchealth.yoda.edge.service.model.User;
import sg.ntuchealth.yoda.edge.web.StatusCodes;

@Component
public class AuthenticationPreFilter implements GlobalFilter {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired private TokenUtil jwtUtil;

  @Autowired private UserService userService;

  @SneakyThrows
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    logger.info("Global Pre Filter executed");

    ServerHttpRequest request = exchange.getRequest();
    final String token = this.validateAndRetrieveAuthHeader(request);

    User user = jwtUtil.validateTokenAndRetrieveUser(token);

    logger.info("Token Validation successful and request is being routed");

    validateUserAssociation(user);

    return chain.filter(
        exchange
            .mutate()
            .request(request.mutate().header("AssociationId", user.getAssociationID()).build())
            .build());
  }

  private String validateAndRetrieveAuthHeader(ServerHttpRequest request) {
    if (!request.getHeaders().containsKey("Authorization"))
      throw new AuthorizationGlobalException(
          HttpStatus.UNAUTHORIZED, StatusCodes.AUTHORIZATION_ERROR.getMessage());

    return request.getHeaders().getOrEmpty("Authorization").get(0);
  }

  private void validateUserAssociation(User user) {
    if (StringUtils.isEmpty(user.getAssociationID())) {
      if (userService.isUserAssociated(user)) {
        throw new AssocationNotFoundGlobalException(
            HttpStatus.UNAUTHORIZED, StatusCodes.ASSOCIATION_NOT_FOUND_IN_TOKEN.getMessage());
      } else {
        throw new AssocationNotFoundGlobalException(
            HttpStatus.UNAUTHORIZED,
            StatusCodes.ASSOCIATION_NOT_FOUND_IN_TOKEN_AND_DB.getMessage());
      }
    }
  }
}
