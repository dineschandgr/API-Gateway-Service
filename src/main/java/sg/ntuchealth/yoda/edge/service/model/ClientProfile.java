package sg.ntuchealth.yoda.edge.service.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfile {

  @NotNull(message = "Uid cannot be null")
  private String uid;

  @NotBlank(message = "Name cannot be empty")
  private String name;

  private String phone_number;

  private String country_code;

  @NotNull(message = "Email cannot be null")
  private String email;
}
