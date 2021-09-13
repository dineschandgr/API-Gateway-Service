package sg.ntuchealth.yoda.edge.service.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;
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

  private String phone_number_last_verified;

  private String email_last_verified;

  private String date_of_birth;

  private String preferences;

  private String memberships;

  private String myinfo_last_verified;

  private String created_at;

  private String updated_at;

  Map<String, Object> association;

  Map<String, Object> metadata;
}
