package sg.ntuchealth.yoda.edge.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** Adding the source header to requests */
@Component
public class RequestHeaderPreFilter implements GlobalFilter {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    LOGGER.debug("Request header filter executed for the headers [nh-source=YODA]");
    return chain.filter(
        exchange
            .mutate()
            .request(exchange.getRequest().mutate().header("nh-source", "YODA").build())
            .build());
  }
}
