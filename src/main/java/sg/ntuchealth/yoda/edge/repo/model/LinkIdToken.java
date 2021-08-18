package sg.ntuchealth.yoda.edge.repo.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@RedisHash(value = "link_id")
public class LinkIdToken {

  private static final long serialVersionUID = 1L;

  private String id;

  private String accessToken;

  private String scope;

  private int expiresIn;

  private LocalDateTime expiryDateTime;

  private String tokenType;

  @TimeToLive private Long expiration;
}
