package sg.ntuchealth.yoda.edge.service.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LinkIdResponse implements DataSerializable {

  private static final long serialVersionUID = 1L;

  private String accessToken;

  private String scope;

  private int expiresIn;

  private LocalDateTime expiryDateTime;

  private String tokenType;

  @Override
  public void writeData(ObjectDataOutput out) throws IOException {
    out.writeString(accessToken);
    out.writeString(String.valueOf(expiryDateTime));
  }

  @Override
  public void readData(ObjectDataInput in) throws IOException {
    accessToken = in.readString();
    expiryDateTime = LocalDateTime.parse(in.readString());
  }
}
