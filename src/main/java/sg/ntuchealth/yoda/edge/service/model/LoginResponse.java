package sg.ntuchealth.yoda.edge.service.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginResponse implements Serializable {
    private boolean success;
    private String message;
    private int statusCode;

}
