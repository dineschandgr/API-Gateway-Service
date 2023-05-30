package sg.ntuchealth.yoda.edge;

import com.amazonaws.services.sns.AmazonSNS;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
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
@ImportResource("classpath:aws-config.xml")
public class Application {

  @Value("${redis.host}")
  private String REDIS_HOST;

  @Value("${redis.port}")
  private Integer REDIS_PORT;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder
        .routes()
        .route(r -> r.path("/profile").and().method(HttpMethod.GET).uri("lb://profile-service"))
        .route(r -> r.path("/profile").and().method(HttpMethod.PUT).uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/health-declaration")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/health-declaration")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/referral-code/send")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/address/{addressid}")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/address/{addressid}")
                    .and()
                    .method(HttpMethod.PUT)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/address")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/address").and().method(HttpMethod.GET).uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/address/type")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/address/{addressId}")
                    .and()
                    .method(HttpMethod.DELETE)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/contact")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/contact/{contactid}")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/contact/{id}")
                    .and()
                    .method(HttpMethod.PUT)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/contact/emergency")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/contact").and().method(HttpMethod.GET).uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/contact/{contactid}")
                    .and()
                    .method(HttpMethod.DELETE)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/linking")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://profile-service"))
        .route(
            r ->
                r.path("/profile/linking").and().method(HttpMethod.GET).uri("lb://profile-service"))
        .route(r -> r.path("/lovedone").and().method(HttpMethod.POST).uri("lb://profile-service"))
        .route(r -> r.path("/lovedone").and().method(HttpMethod.GET).uri("lb://profile-service"))
        .route(
            r -> r.path("/lovedone/{id}").and().method(HttpMethod.PUT).uri("lb://profile-service"))
        .route(
            r -> r.path("/lovedone/{id}").and().method(HttpMethod.GET).uri("lb://profile-service"))
        .route(
            r ->
                r.path("/lovedone/{id}/revoke")
                    .and()
                    .method(HttpMethod.PUT)
                    .uri("lb://profile-service"))
        .route(r -> r.path("/cart").and().method(HttpMethod.GET).uri("lb://cart-service"))
        .route(
            r ->
                r.path("/cart/checkout-info").and().method(HttpMethod.GET).uri("lb://cart-service"))
        .route(r -> r.path("/cart/checkout").and().method(HttpMethod.POST).uri("lb://cart-service"))
        .route(r -> r.path("/cart").and().method(HttpMethod.PUT).uri("lb://cart-service"))
        .route(r -> r.path("/cart").and().method(HttpMethod.DELETE).uri("lb://cart-service"))
        .route(
            r -> r.path("/cart/item/{id}").and().method(HttpMethod.DELETE).uri("lb://cart-service"))
        .route(
            r -> r.path("/cart/applyCoupon").and().method(HttpMethod.PUT).uri("lb://cart-service"))
        .route(
            r -> r.path("/cart/removeCoupon").and().method(HttpMethod.PUT).uri("lb://cart-service"))
        .route(
            r -> r.path("/order/{id}/status").and().method(HttpMethod.GET).uri("lb://cart-service"))
        .route(r -> r.path("/order/{id}").and().method(HttpMethod.GET).uri("lb://cart-service"))
        .route(r -> r.path("/order").and().method(HttpMethod.GET).uri("lb://cart-service"))
        .route(r -> r.path("/order/{id}/qr").and().method(HttpMethod.GET).uri("lb://cart-service"))
        .route(
            r ->
                r.path("/order/{id}/qr-string")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://cart-service"))
        .route(
            r ->
                r.path("/order/{id}/complete")
                    .and()
                    .method(HttpMethod.PUT)
                    .uri("lb://cart-service"))
        .route(
            r ->
                r.path("/order/{id}/abandon").and().method(HttpMethod.PUT).uri("lb://cart-service"))
        .route(
            r ->
                r.path("/order/item/discount/exists/{subsidyId}")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://cart-service"))
        .route(
            r ->
                r.path("/order/{id}/feedback")
                    .and()
                    .method(HttpMethod.PUT)
                    .uri("lb://cart-service"))
        .route(
            r -> r.path("/linkpay/cards").and().method(HttpMethod.GET).uri("lb://payment-service"))
        .route(
            r ->
                r.path("/linkpay/cards/{id}")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://payment-service"))
        .route(
            r ->
                r.path("/linkpay/cards/{id}")
                    .and()
                    .method(HttpMethod.PUT)
                    .uri("lb://payment-service"))
        .route(
            r ->
                r.path("/linkpay/cards/{id}")
                    .and()
                    .method(HttpMethod.DELETE)
                    .uri("lb://payment-service"))
        .route(
            r -> r.path("/linkpay/cards").and().method(HttpMethod.POST).uri("lb://payment-service"))
        .route(
            r ->
                r.path("/linkpay/payments")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://payment-service"))
        .route(
            r ->
                r.path("/linkpay/cards/{id}/authenticate")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://payment-service"))
        .route(
            r ->
                r.path("/linkpay/payments/{id}/authenticate")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://payment-service"))
        .route(
            r ->
                r.path("/linkpay/payment-method")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://payment-service"))
        .route(
            r ->
                r.path("/denticare/client/status")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/register")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/mhq")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/mhq")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/mhq/{mhqId}")
                    .and()
                    .method(HttpMethod.PUT)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/centers")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/appointment/history")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/appointment")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/appointment/{appointmentId}")
                    .and()
                    .method(HttpMethod.DELETE)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/appointment/{appointmentId}/reschedule")
                    .and()
                    .method(HttpMethod.PUT)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/appointment/availableslot")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/appointment")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/denticare/client/resources")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://denticare-service"))
        .route(
            r ->
                r.path("/config/mastercode")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://configuration-service"))
        .route(
            r ->
                r.path("/config/mastercode/list")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://configuration-service"))
        .route(
            r ->
                r.path("/config/mastercode/{id}")
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
        .route(r -> r.path("/intake").uri("lb://booking-service"))
        .route(
            r ->
                r.path("/intake/{id}/sessions")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://booking-service"))
        .route(r -> r.path("/intake/enroll").uri("lb://booking-service"))
        .route(r -> r.path("/intake/enroll/{intake}").uri("lb://booking-service"))
        .route(
            r -> r.path("/yoda-sessions").and().method(HttpMethod.GET).uri("lb://booking-service"))
        .route(
            r ->
                r.path("/yoda-sessions/vacant-slots")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://booking-service"))
        .route(
            r ->
                r.path("/yoda-sessions/schedule")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://booking-service"))
        .route(
            r ->
                r.path("/yoda-sessions/review")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://booking-service"))
        .route(
            r ->
                r.path("/yoda-sessions/reschedule")
                    .and()
                    .method(HttpMethod.PUT)
                    .uri("lb://booking-service"))
        .route(
            r ->
                r.path("/yoda-sessions/cancel")
                    .and()
                    .method(HttpMethod.PUT)
                    .uri("lb://booking-service"))
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
                r.path("/services/sessions")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://subscription-service"))
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
        .route(
            r ->
                r.path("/payment/dbs-pay")
                    .and()
                    .method(HttpMethod.POST)
                    .uri("lb://dbs-icn-mock-service"))
        .route(
            r -> r.path("/attachments").and().method(HttpMethod.GET).uri("lb://attachment-service"))
        .route(
            r ->
                r.path("/subscriptions/rewards/{id}")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://subscription-service"))
        .route(
            r ->
                r.path("/services/list")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://membership-service"))
        .route(
            r ->
                r.path("/gst-configuration/{date}")
                    .and()
                    .method(HttpMethod.GET)
                    .uri("lb://billing-service"))
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
    // corsConfig.addAllowedHeader("*");
    corsConfig.setAllowedHeaders(
        Arrays.asList("Authorization", "Cache-Control", "Content-Type", "view-as"));

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }

  @Profile({"prod"})
  @Bean
  public CorsWebFilter prodCorsWebFilter() {

    final CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(Arrays.asList("https://my.ntuchealth.sg"));
    corsConfig.setMaxAge(3600L);
    corsConfig.setAllowedMethods(Arrays.asList("*"));
    corsConfig.setAllowCredentials(true);
    corsConfig.setAllowedHeaders(
        Arrays.asList("Authorization", "Cache-Control", "Content-Type", "view-as"));

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

  @Bean
  public NotificationMessagingTemplate notificationMessagingTemplate(AmazonSNS amazonSNS) {
    return new NotificationMessagingTemplate(amazonSNS);
  }
}
