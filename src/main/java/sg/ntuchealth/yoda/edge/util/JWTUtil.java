package sg.ntuchealth.yoda.edge.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sg.ntuchealth.yoda.edge.service.model.ClientLoginResponse;

@Component
public class JWTUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private String expirationTime;

  public String generateToken(ClientLoginResponse clientLoginResponse) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("id", clientLoginResponse.getClientId());
    claims.put("name", clientLoginResponse.getClientName());
    return doGenerateToken(claims, clientLoginResponse.getClientEmail());
  }

  /*
  This token is only used for yoda gateway to communicate to downstream services
  */
  private String doGenerateToken(Map<String, Object> claims, String email) {
    /* set default expiration to 1 year */
    long expirationTimeLong =
        (expirationTime != null) ? Long.parseLong(expirationTime) : 31536000000L;

    final Date createdDate = new Date();
    final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(email)
        .setIssuedAt(createdDate)
        .setExpiration(expirationDate)
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }
}
