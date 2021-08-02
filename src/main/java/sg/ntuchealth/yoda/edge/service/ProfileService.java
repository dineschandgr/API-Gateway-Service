package sg.ntuchealth.yoda.edge.service;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sg.ntuchealth.yoda.edge.service.model.AssociationUpdateRequest;
import sg.ntuchealth.yoda.edge.service.model.ProfileResponse;
import sg.ntuchealth.yoda.edge.service.model.UserProfile;

@Service
public class ProfileService {

  private String HTTP_CLIENT_SERVICE_APPLICABLE = "http://profile-service/profile";

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private RestTemplate restTemplate;

  public ResponseEntity<String> validateUser(String associationId) {
    UUID id = UUID.fromString(associationId);
    LOGGER.info("ProfileService validateUser id: {}", id);
    return restTemplate.getForEntity(HTTP_CLIENT_SERVICE_APPLICABLE + "/login/" + id, String.class);
  }

  public ResponseEntity<ProfileResponse> createUserProfile(UserProfile profileRequest) {
    LOGGER.info("ProfileService createUserProfile: {}", profileRequest);
    return restTemplate.postForEntity(
        HTTP_CLIENT_SERVICE_APPLICABLE, profileRequest, ProfileResponse.class);
  }

  public ResponseEntity<ProfileResponse> getAssociation(String uid) {
    LOGGER.info("ProfileService getAssociation id: {}", uid);
    return restTemplate.getForEntity(
        HTTP_CLIENT_SERVICE_APPLICABLE + "/association/" + uid, ProfileResponse.class);
  }

  public void updateAssociation(String uid) {
    LOGGER.info("ProfileService updateAssociation id: {}", uid);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    AssociationUpdateRequest req = AssociationUpdateRequest.builder().associated(true).build();

    HttpEntity<AssociationUpdateRequest> request = new HttpEntity<>(req, headers);

    String url = HTTP_CLIENT_SERVICE_APPLICABLE + "/association/" + uid;

    restTemplate.exchange(url, HttpMethod.PUT, request, AssociationUpdateRequest.class);
  }
}
