package sg.ntuchealth.yoda.edge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import sg.ntuchealth.yoda.edge.exception.ClientNotFoundException;
import sg.ntuchealth.yoda.edge.service.model.ProfileResponse;
import sg.ntuchealth.yoda.edge.service.model.User;
import sg.ntuchealth.yoda.edge.service.model.UserProfile;

import java.net.URISyntaxException;

@Service
@Transactional
public class UserProfileService {

	@Autowired
	private ClientService clientService;

	@Autowired
	private LinkIDBridgeService linkIDBridgeService;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public void validateClient(User user) throws Exception {
    	if (!StringUtils.isEmpty(user.getAssociationID())) {
			//call clientservice to verify user
			ResponseEntity responseEntity = clientService.validateUser(user.getAssociationID());

			if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				LOGGER.error("User does not exist :" + user.getId());
				throw new ClientNotFoundException("User does not exist :");
			}
		} else {
			LOGGER.info("CLient Association does not exist in the DB. calling LinkID Bridge APIs");
			createUserAndSaveAssociation(user);
		}
	}

	public void createUserAndSaveAssociation(User user) throws Exception {

    	UserProfile userProfile = linkIDBridgeService.findByUID(user.getId());
    	LOGGER.info("Response from linkIDBridgeService.findByUID is "+userProfile);
    	ResponseEntity<ProfileResponse> res = clientService.createUserProfile(userProfile);
		ResponseEntity<UserProfile> responseEntity = null;

		if (res.getStatusCode().equals(HttpStatus.OK)) {
			responseEntity = linkIDBridgeService.saveAssociation(user.getId(),res.getBody().getId());
		}

		 LOGGER.info("Response from linkIDBridgeService.saveAssociation is "+responseEntity);

		if(!responseEntity.getStatusCode().equals(HttpStatus.OK)){
			throw new Exception("Client cannot be created in LinkID");
		}

	}
}
