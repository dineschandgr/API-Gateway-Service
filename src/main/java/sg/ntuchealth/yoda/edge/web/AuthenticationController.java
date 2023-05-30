package sg.ntuchealth.yoda.edge.web;

import com.auth0.jwk.JwkException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.ntuchealth.yoda.edge.service.AuthenticationService;
import sg.ntuchealth.yoda.edge.service.model.LoginResponse;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private AuthenticationService authenticationService;

  /*
  Validiate the JWT received from SSO
  */
  @PostMapping("validate")
  public ResponseEntity<LoginResponse> login(@RequestHeader(value = "Authorization") String token)
      throws JwkException, IOException {

    LOGGER.info("Client logged in token : {} ", token);

    int clientStatusCode = authenticationService.authenticateClient(token);

    return new ResponseEntity<>(
        LoginResponse.builder()
            .success(true)
            .message("Token Validation Successful")
            .statusCode(clientStatusCode)
            .build(),
        HttpStatus.ACCEPTED);
  }

  @GetMapping("logout")
  public ResponseEntity<LoginResponse> logout(
      @RequestHeader(value = "Authorization") String token) {

    // TODO: 5/8/21 // blacklist the JWT on logout
    return null;
  }
}
