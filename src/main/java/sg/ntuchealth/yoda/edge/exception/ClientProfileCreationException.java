package sg.ntuchealth.yoda.edge.exception;

public class ClientProfileCreationException extends RuntimeException {

  public ClientProfileCreationException(String message) {
    super(message);
  }

  public ClientProfileCreationException(Exception ex) {
    super(ex);
  }
}
