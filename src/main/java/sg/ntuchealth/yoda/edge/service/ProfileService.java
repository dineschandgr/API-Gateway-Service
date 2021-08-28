package sg.ntuchealth.yoda.edge.service;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sg.ntuchealth.yoda.edge.common.CommonUtils;
import sg.ntuchealth.yoda.edge.service.model.AssociationUpdateRequest;
import sg.ntuchealth.yoda.edge.service.model.ClientLoginResponse;
import sg.ntuchealth.yoda.edge.service.model.ClientProfile;
import sg.ntuchealth.yoda.edge.service.model.ProfileCreateRequest;

@Service
public class ProfileService {

  private String HTTP_CLIENT_SERVICE_APPLICABLE = "http://profile-service/profile";

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private RestTemplate restTemplate;

  public ResponseEntity<ClientLoginResponse> validateUser(String associationId) {
    UUID id = UUID.fromString(associationId);
    LOGGER.info("ProfileService validateUser id: {}", id);
    return restTemplate.getForEntity(
        HTTP_CLIENT_SERVICE_APPLICABLE + "/login/" + id, ClientLoginResponse.class);
  }

  public ResponseEntity<ClientLoginResponse> createUserProfile(ClientProfile clientProfile) {
    ProfileCreateRequest profileRequest =
        ProfileCreateRequest.builder()
            .name(clientProfile.getName())
            .email(clientProfile.getEmail())
            .phoneNumber(clientProfile.getPhoneNumber())
            .countryCode(clientProfile.getCountryCode())
            .uid(clientProfile.getUid())
            .build();
    LOGGER.info("ProfileService createUserProfile: {}", profileRequest);
    return restTemplate.postForEntity(
        HTTP_CLIENT_SERVICE_APPLICABLE, profileRequest, ClientLoginResponse.class);
  }

  public ResponseEntity<String> getAssociation(String uid) {
    LOGGER.info("ProfileService getAssociation id: {}", uid);
    return restTemplate.getForEntity(
        HTTP_CLIENT_SERVICE_APPLICABLE + "/association/" + uid, String.class);
  }

  public void updateAssociation(String uid) {
    LOGGER.info("ProfileService updateAssociation id: {}", uid);

    HttpHeaders headers = CommonUtils.getJsonRequestResponseHeaders();
    AssociationUpdateRequest req = AssociationUpdateRequest.builder().associated(true).build();
    HttpEntity<AssociationUpdateRequest> request = new HttpEntity<>(req, headers);
    String url = HTTP_CLIENT_SERVICE_APPLICABLE + "/association/" + uid;

    restTemplate.exchange(url, HttpMethod.PUT, request, AssociationUpdateRequest.class);
  }
}
