package sg.ntuchealth.yoda.edge.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sg.ntuchealth.yoda.edge.repo.B3TokenRepository;
import sg.ntuchealth.yoda.edge.util.JWTUtil;

@Service
public class TokenUtilService {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired private RestTemplate restTemplate;

  @Autowired private B3TokenRepository b3TokenRepository;

  @Autowired private JWTUtil jwtUtil;

  @Autowired private RedisTemplate redisTemplate;

  public static final String B3_TOKEN = "token_";

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private String expirationTime;

  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  public String getUsernameFromToken(String token) {
    return getAllClaimsFromToken(token).getSubject();
  }

  public Date getExpirationDateFromToken(String token) {
    return getAllClaimsFromToken(token).getExpiration();
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  private String doGenerateToken(Map<String, Object> claims, String username) {
    /* set default expiration to 12 hours */
    long expirationTimeLong = (expirationTime != null) ? Long.parseLong(expirationTime) : 43200000L;

    final Date createdDate = new Date();
    final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(createdDate)
        .setExpiration(expirationDate)
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  public Boolean validateToken(String token) {
    return !isTokenExpired(token);
  }

  public Set<String> getRedisKeys(String token) throws Exception {
    LOGGER.info("In getRedisKeys method: ");

    String ssoToken = token.substring(7);

    if (!validateToken(ssoToken)) throw new Exception("Invalid token");

    return redisTemplate.keys("bumblebee*");
  }

  public void deleteRedisKeys(String token) throws Exception {
    LOGGER.info("In deleteRedisKeys method: ");
    String ssoToken = token.substring(7);

    if (!validateToken(ssoToken)) throw new Exception("Invalid token");

    Set<String> keys = redisTemplate.keys("bumblebee*");

    redisTemplate.delete(keys);
  }
}
