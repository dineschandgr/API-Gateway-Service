package sg.ntuchealth.yoda.edge.service.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class LinkIdRequest {

  private String client_id;

  private String client_secret;

  private String audience;

  private String grant_type;
}
