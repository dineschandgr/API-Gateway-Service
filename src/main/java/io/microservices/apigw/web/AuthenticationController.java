package io.microservices.apigw.web;


import io.microservices.apigw.service.model.LoginDTO;
import io.microservices.apigw.service.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import io.microservices.apigw.service.UserService;
import io.microservices.apigw.util.JwtUtils;

@RestController
@RequestMapping("api")
public class AuthenticationController {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private UserService userService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  JwtUtils jwtUtils;


  @PostMapping("login")
  public Mono<ResponseEntity<String>> authenticate(@RequestBody LoginDTO loginDTO) {
    try {
      return userService.getUserDetails(loginDTO.getEmail()).map((userDetails) -> {
        if (loginDTO.getPassword().equals(userDetails.getPassword())) {
          User user = (User) userDetails;

          String token = jwtUtils.generateToken(user);

          return ResponseEntity.ok(token);
        } else {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
      });
    } catch (HttpClientErrorException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @GetMapping("test")
  public Mono<String> test() {
    return Mono.just("test api works");
  }


}
