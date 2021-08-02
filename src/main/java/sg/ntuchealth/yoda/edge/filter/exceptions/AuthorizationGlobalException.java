package sg.ntuchealth.yoda.edge.filter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorizationGlobalException extends ResponseStatusException {

  private static final long serialVersionUID = 1L;

  public AuthorizationGlobalException(HttpStatus status, String message) {
    super(status, message);
  }
}
