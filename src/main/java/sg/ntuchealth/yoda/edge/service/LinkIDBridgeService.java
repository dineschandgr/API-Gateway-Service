package sg.ntuchealth.yoda.edge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sg.ntuchealth.yoda.edge.common.CommonUtils;
import sg.ntuchealth.yoda.edge.repo.LinkIdRepository;
import sg.ntuchealth.yoda.edge.repo.model.LinkIdToken;
import sg.ntuchealth.yoda.edge.service.model.ClientProfile;
import sg.ntuchealth.yoda.edge.service.model.LinkIdRequest;

@Service
public class LinkIDBridgeService {

  @Value("${linkid.client-id}")
  private String clientId;

  @Value("${linkid.client-secret}")
  private String clientSecret;

  @Value("${link-bridge.url.token}")
  private String HTTPS_LINKBRIDGE_TOKEN;

  @Value("${link-bridge.url.api}")
  private String HTTPS_LINKBRIDGE_API;

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private RestTemplate restTemplateNoLB;

  @Autowired private LinkIdRepository linkIdRepository;

  public static final String LINK_ID_TOKEN = "token";

  // This method will be called only if the entry has expired in the cache. LinkID Token is valid
  // for 24 hours
  private LinkIdToken authenticateClient() {

    HttpHeaders headers = CommonUtils.getJsonRequestResponseHeaders();

    LinkIdRequest linkIdRequest =
        LinkIdRequest.builder()
            .client_id(clientId)
            .client_secret(clientSecret)
            .audience("https://bridge.identity.nedigital.sg")
            .grant_type("client_credentials")
            .build();

    HttpEntity<LinkIdRequest> entity = new HttpEntity<>(linkIdRequest, headers);

    ResponseEntity<LinkIdToken> linkIdResponseEntity =
        restTemplateNoLB.exchange(
            HTTPS_LINKBRIDGE_TOKEN, HttpMethod.POST, entity, LinkIdToken.class);
    LOGGER.info("linkid token obtained from LinkID Bridge API: {} ", linkIdResponseEntity);

    LinkIdToken linkIdToken = linkIdResponseEntity.getBody();
    linkIdToken.setId(LINK_ID_TOKEN);

    // setting TTL for cache entry from the value given by LinkID Token.
    linkIdToken.setExpiration((long) linkIdToken.getExpiresIn());
    return linkIdToken;
  }

  public ClientProfile findByUID(String uid) {
    String token = retrieveAccessToken();
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    HttpEntity<String> entity = new HttpEntity<>(uid, headers);
    String url = HTTPS_LINKBRIDGE_API + "/bridge/user/uid/" + uid;
    ResponseEntity<ClientProfile> res =
        restTemplateNoLB.exchange(url, HttpMethod.GET, entity, ClientProfile.class);

    return res.getBody();
  }

  public ResponseEntity<ClientProfile> saveAssociation(String uid, String associationId)
      throws JsonProcessingException {

    LOGGER.info("In saveAssociation API: {} ", uid);

    String token = retrieveAccessToken();

    HttpHeaders headers = CommonUtils.getJsonRequestResponseHeaders();
    headers.setBearerAuth(token);

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonBody = mapper.createObjectNode();
    jsonBody.put("health", associationId);

    String requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonBody);

    HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
    String url = HTTPS_LINKBRIDGE_API + "/bridge/user/uid/" + uid + "/association";

    return restTemplateNoLB.exchange(url, HttpMethod.PATCH, entity, ClientProfile.class);
  }

  /*
  1. Retrieve token from cache
  2. If not found then call authenticateClient() method to get new token
  3. Save the token in the cache
   */
  public String retrieveAccessToken() {
    LOGGER.info("In retrieveAccessToken method: ");
    LinkIdToken linkIdToken =
        linkIdRepository
            .findById(LINK_ID_TOKEN)
            .orElseGet(() -> linkIdRepository.save(authenticateClient()));
    return linkIdToken.getAccessToken();
  }
}
