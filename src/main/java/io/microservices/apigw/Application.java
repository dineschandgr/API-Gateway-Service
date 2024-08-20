package io.microservices.apigw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder
        .routes()
        .route(r -> r.path("/user/**").uri("lb://user-service"))
        .route(r -> r.path("/address/**").uri("lb://user-service"))
        .route(r -> r.path("/cart/**").uri("lb://order-service"))
        .route(r -> r.path("/order/**").uri("lb://order-service"))
        .route(r -> r.path("/product/**").uri("lb://product-service"))
        .route(r -> r.path("/category/**").uri("lb://product-service"))
        .route(r -> r.path("/payment/**").uri("lb://payment-service"))
        .build();
  }

  @Bean
  @LoadBalanced
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

//  @Bean
//  public WebClient loadBalancedWebClientBuilder() {
//    WebClient webClient = WebClient.builder().baseUrl("http://localhost:8765")
//            .defaultCookie("cookie-name", "cookie-value") // Set a default cookie for the requests
//            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // Set a default header for the requests
//            .build();
//    return webClient;
//  }

}
