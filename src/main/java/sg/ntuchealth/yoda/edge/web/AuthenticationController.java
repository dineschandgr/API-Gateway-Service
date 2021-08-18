package sg.ntuchealth.yoda.edge.web;

import com.auth0.jwk.JwkException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.ntuchealth.yoda.edge.service.AuthenticationManager;
import sg.ntuchealth.yoda.edge.service.B3TokenService;
import sg.ntuchealth.yoda.edge.service.ClientService;
import sg.ntuchealth.yoda.edge.service.model.Client;
import sg.ntuchealth.yoda.edge.service.model.ClientLoginResponse;
import sg.ntuchealth.yoda.edge.service.model.LoginResponse;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private ClientService clientService;

  @Autowired private B3TokenService b3TokenService;

  /*
  Validiate the JWT received from SSO
  */
  @PostMapping("validate")
  public ResponseEntity<LoginResponse> login(@RequestHeader(value = "Authorization") String token)
      throws JwkException, JsonProcessingException {
    String ssoToken = token.substring(7);

    Client client = authenticationManager.authenticate(ssoToken);

    ClientLoginResponse clientLoginResponse = clientService.validateClient(client);

    b3TokenService.generateAndSaveAccessToken(clientLoginResponse);

    return new ResponseEntity<>(
        LoginResponse.builder()
            .success(true)
            .message("Token Validation Successful")
            .statusCode(HttpStatus.OK.value())
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
