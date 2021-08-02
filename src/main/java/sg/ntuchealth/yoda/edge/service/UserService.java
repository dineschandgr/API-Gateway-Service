package sg.ntuchealth.yoda.edge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sg.ntuchealth.yoda.edge.exception.AssociationNotSavedinLinkIDException;
import sg.ntuchealth.yoda.edge.exception.ClientNotFoundException;
import sg.ntuchealth.yoda.edge.service.model.ProfileResponse;
import sg.ntuchealth.yoda.edge.service.model.User;
import sg.ntuchealth.yoda.edge.service.model.UserProfile;
import sg.ntuchealth.yoda.edge.web.StatusCodes;

@Service
@Transactional
public class UserService {

  @Autowired private ProfileService profileService;

  @Autowired private LinkIDBridgeService linkIDBridgeService;

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  public int validateClient(User user) throws JsonProcessingException {
    if (!StringUtils.isEmpty(user.getAssociationID())) {
      // call clientservice to verify user
      ResponseEntity<String> responseEntity = profileService.validateUser(user.getAssociationID());
      LOGGER.info("Existing user Login flow");
      if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
        LOGGER.error("User does not exist : {} ", user.getId());
        throw new ClientNotFoundException("User does not exist");
      }
      return StatusCodes.EXISTING_USER.getCode();
    } else {
      LOGGER.info("Client Association does not exist in the DB. calling LinkID Bridge APIs");
      createUserAndSaveAssociation(user);
      return StatusCodes.NEW_USER.getCode();
    }
  }

  public void createUserAndSaveAssociation(User user) throws JsonProcessingException {

    UserProfile userProfile = linkIDBridgeService.findByUID(user.getId());
    LOGGER.info("Response from linkIDBridgeService.findByUID is: {} ", userProfile);
    ResponseEntity<ProfileResponse> res = profileService.createUserProfile(userProfile);
    ResponseEntity<UserProfile> responseEntity = null;

    if (res.getStatusCode().equals(HttpStatus.OK)) {

      responseEntity = linkIDBridgeService.saveAssociation(user.getId(), res.getBody().getId());

      if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
        throw new AssociationNotSavedinLinkIDException(
            "User Association cannot be saved in LinkID");
      }

      // Save Association Entry in Client_Login table
      profileService.updateAssociation(user.getId());
    }
    LOGGER.info("Response from linkIDBridgeService.saveAssociation is : {} ", responseEntity);
  }

  public boolean isUserAssociated(User user) {
    ResponseEntity<ProfileResponse> responseEntity = profileService.getAssociation(user.getId());
    LOGGER.info("User Service validateClientAssociation ");
    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      LOGGER.error("User does not exist : {} ", user.getId());
      throw new ClientNotFoundException("User does not exist");
    }

    return true;
  }
}
