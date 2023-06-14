package sg.ntuchealth.yoda.edge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sg.ntuchealth.yoda.edge.common.StatusCodes;
import sg.ntuchealth.yoda.edge.exception.AssociationNotSavedinLinkIDException;
import sg.ntuchealth.yoda.edge.exception.ClientNotFoundException;
import sg.ntuchealth.yoda.edge.exception.ClientProfileCreationException;
import sg.ntuchealth.yoda.edge.service.model.Client;
import sg.ntuchealth.yoda.edge.service.model.ClientLoginResponse;
import sg.ntuchealth.yoda.edge.service.model.ClientProfile;

@Service
@Transactional
public class ClientService {

  @Autowired private ProfileService profileService;

  @Autowired private LinkIDBridgeService linkIDBridgeService;

  @Autowired private B3TokenService b3TokenService;

  @Autowired
  private ApplicationAnalyticsEventNotificationService applicationAnalyticsEventNotificationService;

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  public int validateClientAndGenerateB3Token(Client client) throws IOException {
    ResponseEntity<ClientLoginResponse> profileResponseEntity = null;

    LOGGER.info("Logged in client id: {}, client: {} ", client.getId(), client);

    int statusCode;

    if (!StringUtils.isEmpty(client.getAssociationID())) {
      profileResponseEntity =
          profileService.validateAndSaveLastLoginTime(client.getAssociationID());
      LOGGER.info("Existing client Login flow");

      if (!profileResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
        LOGGER.error("Client does not exist : {} ", client.getId());
        throw new ClientNotFoundException("Client does not exist");
      }

      statusCode = StatusCodes.EXISTING_USER.getCode();

    } else {
      LOGGER.info("Client Association does not exist in the DB. calling LinkID Bridge APIs");
      profileResponseEntity = createUserAndSaveAssociation(client);
      statusCode = StatusCodes.NEW_USER.getCode();

      try {
        processNewUserSignUpEventNotification(profileResponseEntity);
      } catch (Exception e) {
        LOGGER.error("Error while sending application analytics to SNS ", e);
      }
    }

    b3TokenService.generateAndSaveAccessToken(profileResponseEntity.getBody());
    return statusCode;
  }

  private void processNewUserSignUpEventNotification(
      ResponseEntity<ClientLoginResponse> profileResponseEntity) {
    Optional<ClientLoginResponse> loginResponse =
        Optional.ofNullable(profileResponseEntity.getBody());
    applicationAnalyticsEventNotificationService.sendNewUserSignUpEventNotification(
        loginResponse.isPresent() ? loginResponse.get().getClientId() : null);
  }

  public ResponseEntity<ClientLoginResponse> createUserAndSaveAssociation(Client client)
      throws JsonProcessingException {

    try {
      ClientProfile clientProfile = linkIDBridgeService.findByUID(client.getId());
      LOGGER.info("Response from linkIDBridgeService.findByUID is: {} ", clientProfile);
      ResponseEntity<ClientLoginResponse> profileResponseEntity =
          profileService.createUserProfile(clientProfile);
      ResponseEntity<ClientProfile> clientResponseEntity = null;

      if (profileResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
        clientResponseEntity =
            linkIDBridgeService.saveAssociation(
                client.getId(), String.valueOf(profileResponseEntity.getBody().getId()));

        if (!clientResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
          throw new AssociationNotSavedinLinkIDException(
              "Client Association cannot be saved in LinkID");
        }

        // Save Association Entry in Client_Login table
        profileService.updateAssociationAndSaveLastLoginTime(client.getId());
      }
      LOGGER.info(
          "Response from linkIDBridgeService.saveAssociation is : {} ", clientResponseEntity);

      return profileResponseEntity;
    } catch (Exception e) {
      throw new ClientProfileCreationException(e);
    }
  }
}
