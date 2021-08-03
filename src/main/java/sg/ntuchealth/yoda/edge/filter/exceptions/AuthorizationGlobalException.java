package sg.ntuchealth.yoda.edge.filter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorizationGlobalException extends ResponseStatusException {

  public AuthorizationGlobalException(HttpStatus status, String message) {
    super(status, message);
  }
}
