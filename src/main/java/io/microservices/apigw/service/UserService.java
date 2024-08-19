package io.microservices.apigw.service;

import io.microservices.apigw.service.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@Service
public class UserService implements ReactiveUserDetailsService {


  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private RestTemplate restTemplate;

  @Autowired
  private WebClient webClient;

  public Mono<User> getUserDetails(String email) throws ExecutionException, InterruptedException {

    ResponseEntity<User> response =
            restTemplate.exchange(
                    "http://user-service/user/email/" + email,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {});

    return response.getStatusCode().isError() ? Mono.empty() : Mono.just(response.getBody());
//      return webClient.get().uri(uribuilder ->
//      uribuilder.host("user-service")
//              .path("/user/email")
//              .queryParam("email",email)
//              .build())
//              .retrieve()
//              .bodyToMono(User.class);
              //.onErrorResume(e -> Mono.empty()).block();

//    return webClient.get().uri("/user/email/{email}",email)
//            .retrieve()
//            .bodyToMono(User.class);

//    System.out.println("userDetails "+user);
//    return Mono.just(user);
  }

  @Override
  public Mono<UserDetails> findByUsername(String email) {
    ResponseEntity<User> response = restTemplate.getForEntity("http://user-service/user/email/" + email, User.class);
    return response.getStatusCode().isError() ? Mono.empty() : Mono.just(response.getBody());
  }
}
