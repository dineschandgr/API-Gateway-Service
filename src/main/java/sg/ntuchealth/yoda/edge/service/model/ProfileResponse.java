package sg.ntuchealth.yoda.edge.service.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
