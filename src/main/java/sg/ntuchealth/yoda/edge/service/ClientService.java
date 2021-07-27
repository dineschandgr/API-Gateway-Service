package sg.ntuchealth.yoda.edge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

import sg.ntuchealth.yoda.edge.service.model.UserProfile;
import sg.ntuchealth.yoda.edge.service.model.ProfileResponse;
import sg.ntuchealth.yoda.edge.service.model.User;

@Service
public class ClientService{

	@Value("${client-service.url}")
	private String HTTP_CLIENT_SERVICE_APPLICABLE;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestTemplate restTemplate;

	public ResponseEntity validateUser(String associationId) {
		String id = UUID.nameUUIDFromBytes( String.valueOf( associationId ).getBytes() ).toString();
		LOGGER.info("ClientService validateUser id "+id);
		ResponseEntity response = restTemplate.getForEntity(HTTP_CLIENT_SERVICE_APPLICABLE + "/login/" + id, User.class);
		return response;
	}

	public ResponseEntity<ProfileResponse> createUserProfile(UserProfile profileRequest) {
		return restTemplate.postForEntity(HTTP_CLIENT_SERVICE_APPLICABLE, profileRequest, ProfileResponse.class);
	}
}
