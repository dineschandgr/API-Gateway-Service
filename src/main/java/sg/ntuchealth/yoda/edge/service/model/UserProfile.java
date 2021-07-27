package sg.ntuchealth.yoda.edge.service.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserProfile {

	@NotNull(message = "Uid cannot be null")
	private String uid;

	@NotBlank(message = "Name cannot be empty")
	private String name;

	private String phoneNumber;

	private String countryCode;

	@NotNull(message = "Email cannot be null")
	private String email;

	private String phoneNumberLastVerified;

	private String emailLastVerified;

	private String dateOfBirth;

	private String preferences;

	private String memberships;

	private String myinfoLastVerified;

	private String createdAt;

	private String updatedAt;

	Map<String, Object> association;

	Map<String, Object> metadata;
}
