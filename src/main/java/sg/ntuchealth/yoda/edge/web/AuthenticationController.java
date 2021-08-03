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
import sg.ntuchealth.yoda.edge.service.UserService;
import sg.ntuchealth.yoda.edge.service.model.LoginResponse;
import sg.ntuchealth.yoda.edge.service.model.User;
import sg.ntuchealth.yoda.edge.util.SSOTokenUtil;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

  @Autowired private SSOTokenUtil jwtUtil;

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private UserService userService;

  /*
  Validiate the JWT received from SSO
  */
  @PostMapping("validate")
  public ResponseEntity<LoginResponse> login(@RequestHeader(value = "Authorization") String token)
      throws JwkException, JsonProcessingException {
    String ssoToken = token.substring(7);

    User user = authenticationManager.authenticate(ssoToken);
    int statusCode = userService.validateClient(user);

    return new ResponseEntity<>(
        LoginResponse.builder()
            .success(true)
            .message("Token Validation Successful")
            .statusCode(statusCode)
            .build(),
        HttpStatus.ACCEPTED);
  }

  @GetMapping("logout")
  public ResponseEntity<LoginResponse> logout(
      @RequestHeader(value = "Authorization") String token) {
    // blacklist the JWT on logout
    return null;
  }
}
