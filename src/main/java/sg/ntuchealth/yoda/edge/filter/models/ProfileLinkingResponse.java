package sg.ntuchealth.yoda.edge.filter.models;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import sg.ntuchealth.yoda.edge.service.model.ClientLoginResponse;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProfileLinkingResponse {

  private UUID clientId;

  private String name;

  private boolean isDuplicateProfileFound;

  private boolean isLovedOneProfileMapped;

  private LinkingType linkingType;

  private ClientLoginResponse clientLoginResponse;
}
