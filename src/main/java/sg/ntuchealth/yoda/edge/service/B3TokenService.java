package sg.ntuchealth.yoda.edge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sg.ntuchealth.yoda.edge.exception.B3TokenNotFoundException;
import sg.ntuchealth.yoda.edge.repo.B3TokenRepository;
import sg.ntuchealth.yoda.edge.repo.model.B3Token;
import sg.ntuchealth.yoda.edge.service.model.ClientDetailsResponse;
import sg.ntuchealth.yoda.edge.service.model.ClientLoginResponse;
import sg.ntuchealth.yoda.edge.util.JWTUtil;

import java.util.UUID;

@Service
public class B3TokenService {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private RestTemplate restTemplate;

  @Autowired private B3TokenRepository b3TokenRepository;

  @Autowired private JWTUtil jwtUtil;

  private String HTTP_CLIENT_SERVICE_APPLICABLE = "http://membership-service/clients";

  @Value("${jwt.expiration}")
  private String expirationTime;

  public static final String B3_TOKEN = "token_";
  public static final String B3_VIEW_AS_TOKEN = "view_as_token_";

  // Save B3 JWT token to cache
  public void generateAndSaveAccessToken(ClientLoginResponse clientLoginResponse) {
    LOGGER.info("In generateAndSaveAccessToken method: {} ", clientLoginResponse.getClientId());

    b3TokenRepository
        .findById(B3_TOKEN + clientLoginResponse.getId())
        .orElseGet(() -> b3TokenRepository.save(generateToken(clientLoginResponse)));
  }

  public B3Token retrieveViewAsAccessToken(String clientId) {
    LOGGER.info("In retrieveViewAsAccessToken method: {} ", clientId);
    return b3TokenRepository
        .findById(B3_VIEW_AS_TOKEN + clientId)
        .orElseGet(() -> b3TokenRepository.save(generateViewAsToken(clientId)));
  }

  public B3Token retrieveAccessToken(String associationId) {
    LOGGER.info("In retrieveAccessToken method: ");
    return b3TokenRepository
        .findById(B3_TOKEN + associationId)
        .orElseThrow(() -> new B3TokenNotFoundException("B3 Access Token not found in cache"));
  }

  public B3Token generateToken(ClientLoginResponse clientLoginResponse) {
    LOGGER.info("In generateToken method: {} ", clientLoginResponse.getClientId());
    String accessToken = jwtUtil.generateToken(clientLoginResponse);
    Long expirationSeconds = Long.parseLong(expirationTime) / 1000;
    return B3Token.builder()
        .id(B3_TOKEN + clientLoginResponse.getId())
        .accessToken(accessToken)
        .expiration(expirationSeconds)
        .clientId(clientLoginResponse.getClientId())
        .build();
  }

  public B3Token generateViewAsToken(String clientId) {
    LOGGER.info("In generateViewAsToken method: {} ", clientId);
    ClientDetailsResponse clientDetails = this.getClientDetails(clientId);

    String accessToken = jwtUtil.generateToken(
        ClientLoginResponse.builder()
            .clientId(clientDetails.getId())
            .clientName(clientDetails.getName())
            .clientEmail(clientDetails.getEmail())
            .build()
    );
    Long expirationSeconds = Long.parseLong(expirationTime) / 1000;
    return B3Token.builder()
        .id(B3_TOKEN + clientDetails.getId())
        .accessToken(accessToken)
        .expiration(expirationSeconds)
        .clientId(clientDetails.getId())
        .build();
  }

  // Re-generate token after Profile is Linked
  public void regenerateToken(ClientLoginResponse clientLoginResponse) {
    LOGGER.info("In regenerateToken method: {} ", clientLoginResponse.getClientId());
    b3TokenRepository.save(generateToken(clientLoginResponse));
  }

  public ClientDetailsResponse getClientDetails(String clientId) {
    try {
      UUID id = UUID.fromString(clientId);
      LOGGER.info("Membership-service get client details id: {}", id);
      ResponseEntity<ClientDetailsResponse> clientDetails =
          restTemplate.getForEntity(
              HTTP_CLIENT_SERVICE_APPLICABLE + "/" + id, ClientDetailsResponse.class);
      return clientDetails.getBody();
    } catch (HttpClientErrorException e) {
      LOGGER.error("Client not found with ID: " + clientId , e);
      throw e;
    }
  }
}
