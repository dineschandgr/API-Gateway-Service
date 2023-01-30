package sg.ntuchealth.yoda.edge.service;

import com.auth0.jwk.JwkException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sg.ntuchealth.yoda.edge.service.model.Client;
import sg.ntuchealth.yoda.edge.util.JWTUtil;
import sg.ntuchealth.yoda.edge.util.SSOTokenUtil;

@Component
public class AuthenticationService {

  @Autowired private SSOTokenUtil ssoTokenUtil;

  @Autowired private JWTUtil jwtUtil;

  @Autowired private ClientService clientService;

  @Autowired private B3TokenService b3TokenService;

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  public int authenticateClient(String token) throws JwkException, JsonProcessingException {

    String ssoToken = token.substring(7);

    ssoTokenUtil.isTokenValid(ssoToken);

    Client client = ssoTokenUtil.retrieveUserFromToken();

    LOGGER.info("Authenticate Client Info: {} ", client);

    return clientService.validateClientAndGenerateB3Token(client);
  }
}
