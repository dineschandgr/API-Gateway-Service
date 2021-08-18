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
public class ClientLoginResponse {

  private UUID id;

  private UUID clientId;

  private String ssoUid;

  private boolean associated;

  private String clientName;

  private String clientEmail;
}
