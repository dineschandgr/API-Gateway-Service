package sg.ntuchealth.yoda.edge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sg.ntuchealth.yoda.edge.config.CacheClient;
import sg.ntuchealth.yoda.edge.service.model.LinkIdRequest;
import sg.ntuchealth.yoda.edge.service.model.LinkIdResponse;
import sg.ntuchealth.yoda.edge.service.model.ProfileResponse;
import sg.ntuchealth.yoda.edge.service.model.UserProfile;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class LinkIDBridgeService{

	@Value("${linkid.client-id}")
	private String clientId;

	@Value("${linkid.client-secret}")
	private String clientSecret;

	@Value("${link-bridge.url.token}")
	private String HTTPS_LINKBRIDGE_TOKEN;

	@Value("${link-bridge.url.api}")
	private String HTTPS_LINKBRIDGE_API;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestTemplate restTemplateNoLB;

	@Autowired
	private CacheClient cacheClient;

	private LinkIdResponse authenticateClient(){

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		LinkIdRequest linkIdRequest = LinkIdRequest.builder().client_id(clientId).client_secret(clientSecret).
				audience("https://bridge.identity.nedigital.sg").grant_type("client_credentials").build();

		HttpEntity<LinkIdRequest> entity = new HttpEntity<>(linkIdRequest , headers);
		LinkIdResponse linkIdResponse = restTemplateNoLB.postForObject(HTTPS_LINKBRIDGE_TOKEN, entity, LinkIdResponse.class);
		LOGGER.info("linkid token obtained from LinkID Bridge API "+linkIdResponse);
		return linkIdResponse;
	}

	public UserProfile findByUID(String uid) {
		String token = retrieveAccessToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);

		HttpEntity<String> entity = new HttpEntity<>(uid , headers);
		String url = HTTPS_LINKBRIDGE_API + "/bridge/user/uid/" + uid;
		ResponseEntity<UserProfile> res = restTemplateNoLB.exchange(url, HttpMethod.GET, entity, UserProfile.class);

		return res.getBody();
	}

	public ResponseEntity<UserProfile> saveAssociation(String uid, String associationId) throws JsonProcessingException {
		LOGGER.info("In saveAssociation API "+uid);
		String token = retrieveAccessToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jsonBody = mapper.createObjectNode();
		jsonBody.put("health", associationId.toString());

		String requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonBody);

		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		String url = HTTPS_LINKBRIDGE_API + "/bridge/user/uid/" + uid + "/association";

		return restTemplateNoLB.exchange(url, HttpMethod.PATCH, entity, UserProfile.class);
	}

	/*
	1. First time write to cache. cachedResponse is null
	2. next time check cachedResponse. if its null or expired, write to cache
	3. else retrieve from cache
	 */
	public String retrieveAccessToken(){
		LocalDateTime currentDateTime = LocalDateTime.now();
		LinkIdResponse cachedResponse = cacheClient.get("token");
		LinkIdResponse linkIdResponse = cachedResponse;

		if(cachedResponse == null || (cachedResponse != null && cachedResponse.getExpiryDateTime().isBefore(currentDateTime))){
			linkIdResponse = authenticateClient();
			LocalDateTime expiryDateTime = currentDateTime.plus(Duration.ofSeconds(linkIdResponse.getExpiresIn()));
			linkIdResponse.setExpiryDateTime(expiryDateTime);
			LinkIdResponse res = cacheClient.put("token", linkIdResponse);
			LOGGER.info("Token obtained from LinkId Bridge API " + linkIdResponse.getTokenType());
		}

		return linkIdResponse.getAccessToken();
	}

}
