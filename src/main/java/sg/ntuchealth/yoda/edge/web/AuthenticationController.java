package sg.ntuchealth.yoda.edge.web;

import com.auth0.jwk.JwkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import sg.ntuchealth.yoda.edge.service.AuthenticationManager;
import sg.ntuchealth.yoda.edge.service.UserProfileService;
import sg.ntuchealth.yoda.edge.service.model.LoginResponse;
import sg.ntuchealth.yoda.edge.service.model.User;
import sg.ntuchealth.yoda.edge.util.SSOTokenUtil;

import java.net.URISyntaxException;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

	@Autowired
	private SSOTokenUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserProfileService edgeService;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	/*
		Validiate the JWT received from SSO
	 */
	@PostMapping("validate")
	public ResponseEntity<LoginResponse> login(@RequestHeader(value = "Authorization") String token) throws Exception {
		String ssoToken = token.substring(7);

		User user = authenticationManager.authenticate(ssoToken);
        edgeService.validateClient(user);

		return new ResponseEntity(LoginResponse.builder()
				.success(true)
				.message("Token Validation Successful")
				.build(),
				HttpStatus.ACCEPTED);
	}

	@GetMapping("logout")
	public ResponseEntity<LoginResponse> logout(@RequestHeader(value = "Authorization") String token) {
		//blacklist the JWT on logout
		return null;
	}
}
