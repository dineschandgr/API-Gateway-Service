package sg.ntuchealth.yoda.edge.util;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import sg.ntuchealth.yoda.edge.exception.TokenExpiredException;
import sg.ntuchealth.yoda.edge.service.model.User;

import javax.annotation.PostConstruct;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.stream.Collectors;

@Component
public class SSOTokenUtil {

	@Value("${jwt.expiration}")
	private String expirationTime;

	@Value("${jwks.url.pre-prod}")
	private String jwksUrl;

	@Value("${claims.identity.uid}")
	private String uid;

	@Value("${claims.identity.associationId}")
	private String associationID;

	@Value("${claims.identity.audience}")
	private String audience;

	private JwkProvider provider;

	private DecodedJWT jwt;

	@PostConstruct
	private void postConstruct() {
		provider = new UrlJwkProvider(jwksUrl);
	}

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public Boolean isTokenValid(String token) throws JwkException {

			jwt = JWT.decode(token);

			// Check expiration
			if (jwt.getExpiresAt().before(Calendar.getInstance().getTime()))
				throw new TokenExpiredException("The token has expired");

			//validate audience
			if (!validateAudience(jwt))
				throw new JwkException("Invalid audience in the token!");

			Jwk jwk = provider.get(jwt.getKeyId());
			Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
			algorithm.verify(jwt);

			return true;
	}

	 public User retrieveUserFromToken(){
        return User.builder().id(jwt.getClaim(uid).asString()).associationID(jwt.getClaim(associationID).asString()).build();
	 }


	public Boolean validateAudience(DecodedJWT jwt) {
		int count = jwt.getAudience().stream()
				.filter(aud -> aud.equals(audience))
				.collect(Collectors.toList()).size();

		return count > 0 ? true : false;
	}


	public Boolean validateScope(DecodedJWT jwt) {
		return false;
	}
}