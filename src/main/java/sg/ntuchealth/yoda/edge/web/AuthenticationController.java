package sg.ntuchealth.yoda.edge.web;

import com.auth0.jwk.JwkException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.ntuchealth.yoda.edge.repo.model.B3Token;
import sg.ntuchealth.yoda.edge.service.AuthenticationService;
import sg.ntuchealth.yoda.edge.service.B3TokenService;
import sg.ntuchealth.yoda.edge.service.model.LoginResponse;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

  @Autowired private AuthenticationService authenticationService;

  @Autowired private B3TokenService b3TokenService;

  /*
  Validiate the JWT received from SSO
  */
  @PostMapping("validate")
  public ResponseEntity<LoginResponse> login(@RequestHeader(value = "Authorization") String token)
      throws JwkException, JsonProcessingException {

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

  @GetMapping("validate/{id}")
  public ResponseEntity<B3Token> validate(@PathVariable(value = "id") String id) {

    return ResponseEntity.ok(b3TokenService.retrieveAccessToken(id));
  }
}
