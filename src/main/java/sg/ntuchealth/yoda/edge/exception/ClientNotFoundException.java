package sg.ntuchealth.yoda.edge.exception;

public class ClientNotFoundException extends RuntimeException {

  public ClientNotFoundException(String message) {
    super(message);
  }
}
