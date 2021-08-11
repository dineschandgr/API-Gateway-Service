package sg.ntuchealth.yoda.edge.service;

import com.auth0.jwk.JwkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sg.ntuchealth.yoda.edge.service.model.User;
import sg.ntuchealth.yoda.edge.util.SSOTokenUtil;

@Component
public class AuthenticationManager {

  @Autowired private SSOTokenUtil ssoTokenUtil;

  public User authenticate(String token) throws JwkException {
    ssoTokenUtil.isTokenValid(token);
    return ssoTokenUtil.retrieveUserFromToken();
  }
}
