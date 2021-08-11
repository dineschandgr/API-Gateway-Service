package sg.ntuchealth.yoda.edge.filter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AssocationNotFoundGlobalException extends ResponseStatusException {

  public AssocationNotFoundGlobalException(HttpStatus status, String message) {
    super(status, message);
  }
}
