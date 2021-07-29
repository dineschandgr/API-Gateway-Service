package sg.ntuchealth.yoda.edge.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum StatusCodes {

    EXISTING_USER("User Token Validated", 1000),
    NEW_USER("User Association Created",1001),
    ASSOCIATION_NOT_FOUND_IN_TOKEN("Association ID not found in token",1002),
    TOKEN_EXPIRED("The token has expired",1003);

    private final String key;
    private final Integer value;

}