package sg.ntuchealth.yoda.edge.filter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TokenExpiredGlobalException extends ResponseStatusException {

  public TokenExpiredGlobalException(HttpStatus status, String message) {
    super(status, message);
  }
}
