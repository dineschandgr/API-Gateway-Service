package sg.ntuchealth.yoda.edge.service.model;

import java.util.List;
import lombok.*;

@Builder
@Data
public class User {

  private String id;
  private String associationID;
  private List<String> audience;
  private List<String> scope;
}
