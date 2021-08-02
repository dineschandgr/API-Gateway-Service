package sg.ntuchealth.yoda.edge.service.model;

import java.io.Serializable;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {
  private boolean success;
  private String message;
  private int statusCode;
}
