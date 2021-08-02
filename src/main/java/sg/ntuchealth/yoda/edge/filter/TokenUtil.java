package sg.ntuchealth.yoda.edge.filter;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import sg.ntuchealth.yoda.edge.filter.exceptions.AuthorizationGlobalException;
import sg.ntuchealth.yoda.edge.filter.exceptions.TokenExpiredGlobalException;
import sg.ntuchealth.yoda.edge.service.model.User;
import sg.ntuchealth.yoda.edge.web.StatusCodes;

@Component
public class TokenUtil {

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

  public User validateTokenAndRetrieveUser(String token) throws JwkException {

    String ssoToken = token.substring(7);

    jwt = JWT.decode(ssoToken);

    // Check expiration
    if (Boolean.TRUE.equals(isTokenExpired()))
      throw new TokenExpiredGlobalException(
          HttpStatus.UNAUTHORIZED, StatusCodes.TOKEN_EXPIRED.getMessage());

    // validate audience
    if (Boolean.FALSE.equals(validateAudience(jwt)))
      throw new AuthorizationGlobalException(
          HttpStatus.UNAUTHORIZED, StatusCodes.AUTHORIZATION_ERROR.getMessage());

    Jwk jwk = provider.get(jwt.getKeyId());
    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
    algorithm.verify(jwt);

    return retrieveUserFromToken();
  }

  public User retrieveUserFromToken() {

    return User.builder()
        .id(jwt.getClaim(uid).asString())
        .associationID(jwt.getClaim(associationID).asString())
        .build();
  }

  public Boolean validateAudience(DecodedJWT jwt) {
    int count =
        jwt.getAudience().stream()
            .filter(aud -> aud.equals(audience))
            .collect(Collectors.toList())
            .size();

    return count > 0;
  }

  public Boolean isTokenExpired() {
    return jwt.getExpiresAt().before(Calendar.getInstance().getTime());
  }

  public Boolean validateScope() {
    return false;
  }
}
