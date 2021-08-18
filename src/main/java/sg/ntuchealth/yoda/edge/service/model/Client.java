package sg.ntuchealth.yoda.edge.service.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Client {

  private String id;
  private String associationID;
  private List<String> audience;
  private List<String> scope;
}
