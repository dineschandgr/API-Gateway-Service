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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import sg.ntuchealth.yoda.edge.common.StatusCodes;
import sg.ntuchealth.yoda.edge.exception.B3TokenNotFoundException;
import sg.ntuchealth.yoda.edge.filter.exceptions.AssocationNotFoundGlobalException;
import sg.ntuchealth.yoda.edge.filter.exceptions.AuthorizationGlobalException;
import sg.ntuchealth.yoda.edge.repo.model.B3Token;
import sg.ntuchealth.yoda.edge.service.B3TokenService;
import sg.ntuchealth.yoda.edge.service.ClientService;
import sg.ntuchealth.yoda.edge.service.ProfileService;
import sg.ntuchealth.yoda.edge.service.model.Client;
import sg.ntuchealth.yoda.edge.service.model.ClientLoginResponse;

@Component
public class AuthenticationPreFilter implements GlobalFilter {

  public static final String AUTHORIZATION = "Authorization";
  public static final String VIEW_AS = "view-as";
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private TokenUtil jwtUtil;

  @Autowired private ClientService clientService;

  @Autowired private B3TokenService b3TokenService;

  @Autowired private ProfileService profileService;

  @SneakyThrows
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    LOGGER.info("Global Pre Filter executed");

    ServerHttpRequest request = exchange.getRequest();
    final String token = this.validateAndRetrieveAuthHeader(request);

    Client client = jwtUtil.validateTokenAndRetrieveUser(token);

    LOGGER.info("Token Validation successful and request is being routed");

    validateUserAssociation(client);

    // View-as feature is only enabled for admin
    // Requests will be associated with UUID specified in view-as header
    if (request.getHeaders().containsKey(VIEW_AS)) {
      ClientLoginResponse clientProfile = profileService.validateUser(client.getAssociationID()).getBody();
      if (clientProfile.isAdmin()) {
        String viewAsClientId = request.getHeaders().getFirst(VIEW_AS);
        B3Token b3Token = b3TokenService.retrieveViewAsAccessToken(viewAsClientId);
        return chain.filter(
            exchange
                .mutate()
                .request(
                    request
                        .mutate()
                        .header("AssociationId", client.getAssociationID())
                        .header("ClientId", String.valueOf(b3Token.getClientId()))
                        .header("SsoUid", client.getId())
                        .header(AUTHORIZATION, "Bearer " + b3Token.getAccessToken())
                        .build())
                .build());
      }
    }

    B3Token b3Token = b3TokenService.retrieveAccessToken(client.getAssociationID());
    return chain.filter(
        exchange
            .mutate()
            .request(
                request
                    .mutate()
                    .header("AssociationId", client.getAssociationID())
                    .header("ClientId", String.valueOf(b3Token.getClientId()))
                    .header("SsoUid", client.getId())
                    .header(AUTHORIZATION, "Bearer " + b3Token.getAccessToken())
                    .build())
            .build());
  }

  private String validateAndRetrieveAuthHeader(ServerHttpRequest request) {
    if (!request.getHeaders().containsKey(AUTHORIZATION))
      throw new AuthorizationGlobalException(
          HttpStatus.UNAUTHORIZED, StatusCodes.AUTHORIZATION_ERROR.getMessage());

    return request.getHeaders().getOrEmpty(AUTHORIZATION).get(0);
  }

  private void validateUserAssociation(Client client) {
    if (StringUtils.isEmpty(client.getAssociationID())) {
      throw new AssocationNotFoundGlobalException(
          HttpStatus.UNAUTHORIZED, StatusCodes.ASSOCIATION_NOT_FOUND_IN_TOKEN.getMessage());
    }
  }
}
