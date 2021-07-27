package sg.ntuchealth.yoda.edge.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LinkIdRequest {

    @JsonProperty
    private String client_id;

    @JsonProperty
    private String client_secret;

    @JsonProperty
    private String audience;

    @JsonProperty
    private String grant_type;
}