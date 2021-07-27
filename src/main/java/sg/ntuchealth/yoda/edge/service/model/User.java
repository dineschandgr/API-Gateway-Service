package sg.ntuchealth.yoda.edge.service.model;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User{

	private String id;
    private String associationID;
	private List<String> audience;
	private List<String> scope;

}
