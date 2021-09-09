package sg.ntuchealth.yoda.edge.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;
import sg.ntuchealth.yoda.edge.filter.models.LinkingType;
import sg.ntuchealth.yoda.edge.filter.models.ProfileLinkingResponse;
import sg.ntuchealth.yoda.edge.service.B3TokenService;
import sg.ntuchealth.yoda.edge.service.model.ClientLoginResponse;

@Configuration
public class PostFilter {

  @Autowired protected ObjectMapper objectMapper;

  @Autowired protected B3TokenService b3TokenService;

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Bean
  public GlobalFilter postGlobalFilter() {
    return (exchange, chain) ->
        chain
            .filter(exchange)
            .then(
                Mono.fromRunnable(
                    () -> {
                      LOGGER.info("Global Post Filter executed");
                    }));
  }

  @Bean
  public RouteLocator retrieveResponseBody(RouteLocatorBuilder builder) {
    return builder
        .routes()
        .route(
            r ->
                r.path("/profile/linking")
                    .and()
                    .method(HttpMethod.POST) // your own path filter
                    .filters(
                        f ->
                            f.modifyResponseBody(
                                String.class,
                                String.class,
                                (webExchange, originalBody) -> {
                                  if (originalBody != null) {
                                    LOGGER.debug("Response body {}", originalBody);
                                    try {
                                      ProfileLinkingResponse profileLinkingResponse =
                                          objectMapper.readValue(
                                              originalBody, ProfileLinkingResponse.class);
                                      if (LinkingType.MYSELF.equals(
                                          profileLinkingResponse.getLinkingType())) {
                                        ClientLoginResponse clientLoginResponse =
                                            profileLinkingResponse.getClientLoginResponse();
                                        b3TokenService.regenerateToken(clientLoginResponse);
                                        LOGGER.debug(
                                            "A new token generated for client after profile linking is  {}",
                                            clientLoginResponse);
                                      }

                                      LOGGER.debug(
                                          "Response body clientid {}",
                                          profileLinkingResponse.getClientId());
                                    } catch (JsonProcessingException e) {
                                      LOGGER.error("Error in Json processing {} ", e.getMessage());
                                    }
                                    return Mono.just(originalBody);
                                  } else {
                                    return Mono.empty();
                                  }
                                }))
                    .uri("lb://profile-service"))
        .build();
  }
}
