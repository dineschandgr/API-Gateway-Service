package sg.ntuchealth.yoda.edge.service.model;

import java.util.UUID;
import lombok.*;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

  private String id;

  private String ssoUid;

  private UUID clientId;

  private boolean associated;
}
