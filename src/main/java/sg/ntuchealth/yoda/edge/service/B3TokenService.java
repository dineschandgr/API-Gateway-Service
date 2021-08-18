package sg.ntuchealth.yoda.edge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sg.ntuchealth.yoda.edge.exception.B3TokenNotFoundException;
import sg.ntuchealth.yoda.edge.repo.B3TokenRepository;
import sg.ntuchealth.yoda.edge.repo.model.B3Token;
import sg.ntuchealth.yoda.edge.service.model.ClientLoginResponse;
import sg.ntuchealth.yoda.edge.util.JWTUtil;

@Service
public class B3TokenService {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private RestTemplate restTemplate;

  @Autowired private B3TokenRepository b3TokenRepository;

  @Autowired private JWTUtil jwtUtil;

  @Value("${jwt.expiration}")
  private String expirationTime;

  public static final String B3_TOKEN = "token_";

  // Save B3 JWT token to cache
  public void generateAndSaveAccessToken(ClientLoginResponse clientLoginResponse) {
    LOGGER.info("In generateAndSaveAccessToken method: {} ", clientLoginResponse.getClientId());

    b3TokenRepository
        .findById(B3_TOKEN + clientLoginResponse.getId())
        .orElseGet(() -> b3TokenRepository.save(generateToken(clientLoginResponse)));
  }

  public String retrieveAccessToken(String clientId) {
    LOGGER.info("In retrieveAccessToken method: ");
    B3Token b3Token =
        b3TokenRepository
            .findById(B3_TOKEN + clientId)
            .orElseThrow(() -> new B3TokenNotFoundException("B3 Access Token not found in cache"));
    return b3Token.getAccessToken();
  }

  public B3Token generateToken(ClientLoginResponse clientLoginResponse) {
    LOGGER.info("In generateToken method: {} ", clientLoginResponse.getClientId());
    String accessToken = jwtUtil.generateToken(clientLoginResponse);
    Long expirationSeconds = Long.parseLong(expirationTime) / 1000;
    return B3Token.builder()
        .id(B3_TOKEN + clientLoginResponse.getId())
        .accessToken(accessToken)
        .expiration(expirationSeconds)
        .build();
  }
}
