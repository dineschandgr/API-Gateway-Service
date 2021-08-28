package sg.ntuchealth.yoda.edge.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCreateRequest {

  private String name;

  private String email;

  private String phoneNumber;

  private String countryCode;

  private String uid;
}
