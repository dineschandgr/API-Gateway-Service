package sg.ntuchealth.yoda.edge.service;

import com.auth0.jwk.JwkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sg.ntuchealth.yoda.edge.service.model.Client;
import sg.ntuchealth.yoda.edge.util.JWTUtil;
import sg.ntuchealth.yoda.edge.util.SSOTokenUtil;

@Component
public class AuthenticationManager {

  @Autowired private SSOTokenUtil ssoTokenUtil;

  @Autowired private JWTUtil jwtUtil;

  @Autowired private B3TokenService b3TokenService;

  public Client authenticate(String token) throws JwkException {
    ssoTokenUtil.isTokenValid(token);
    return ssoTokenUtil.retrieveUserFromToken();
  }
}
