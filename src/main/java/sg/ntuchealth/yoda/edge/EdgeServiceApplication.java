package sg.ntuchealth.yoda.edge;

import java.util.Arrays;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableDiscoveryClient
public class EdgeServiceApplication {

  @Value("${redis.host}")
  private String REDIS_HOST;

  @Value("${redis.port}")
  private Integer REDIS_PORT;

  public static void main(String[] args) {
    SpringApplication.run(EdgeServiceApplication.class, args);
  }

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder
        .routes()
        .route(r -> r.path("/profile/**").uri("lb://profile-service"))
        .route(r -> r.path("/lovedone/**").uri("lb://profile-service"))
        .route(r -> r.path("/cart/**").uri("lb://cart-service"))
        .route(r -> r.path("/order/**").uri("lb://cart-service"))
        .route(
            r ->
                r.path("/config/mastercode/**")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://configuration-service"))
        .route(
            r ->
                r.path("/products/category")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://membership-service"))
        .route(
            r ->
                r.path("/products/category/{category}")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://membership-service"))
        .route(
            r ->
                r.path("/products/category/{category}/{subcategory}")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://membership-service"))
        .route(
            r ->
                r.path("/products/{id}/centers/list")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://membership-service"))
        .route(
            r ->
                r.path("/products/v2/{id}/events")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://membership-service"))
        .route(
            r ->
                r.path("/groups/v2/{id}")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://organization-service"))
        .route(
            r ->
                r.path("/products-ext/{id}/offerings/list")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://subscription-service"))
        .route(
            r ->
                r.path("/products/v2/{id}")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://membership-service"))
        .route(r -> r.path("/intake").and().method(HttpMethod.GET).uri("lb://booking-service"))
        .route(r -> r.path("/intake/enroll").uri("lb://booking-service"))
        .route(
            r -> r.path("/yoda-sessions").and().method(HttpMethod.GET).uri("lb://booking-service"))
        .route(
            r ->
                r.path("/subscriptions/{id}/next")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://subscription-service"))
        .route(
            r -> r.path("/services").and().method(HttpMethod.GET).uri("lb://subscription-service"))
        .route(
            r ->
                r.path("/groups/locations")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://organization-service"))
        .route(
            r ->
                r.path("/products/list")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://membership-service"))
        .route(
            r -> r.path("/appointment").and().method(HttpMethod.GET).uri("lb://membership-service"))
        .build();
  }

  @Bean
  @LoadBalanced
  public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
    return new RestTemplate(factory);
  }

  @Bean
  public RestTemplate restTemplateNoLB(ClientHttpRequestFactory factory) {
    return new RestTemplate(factory);
  }

  @Bean
  public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(5000);
    return factory;
  }

  @Bean
  @LoadBalanced
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }

  @Profile({"alpha", "uat"})
  @Bean
  public CorsWebFilter corsWebFilter() {

    final CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(Collections.singletonList("*"));
    corsConfig.setMaxAge(3600L);
    corsConfig.setAllowedMethods(Arrays.asList("*"));
    corsConfig.addAllowedHeader("*");

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }

  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(REDIS_HOST, REDIS_PORT);
    return new JedisConnectionFactory(config);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisConnectionFactory());
    template.setKeySerializer(new StringRedisSerializer());
    return template;
  }
}
