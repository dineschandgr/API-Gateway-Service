package sg.ntuchealth.yoda.edge.filter.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LinkingType {
  MYSELF("myself"),
  LOVEDONE("lovedone");

  private String value;
}
