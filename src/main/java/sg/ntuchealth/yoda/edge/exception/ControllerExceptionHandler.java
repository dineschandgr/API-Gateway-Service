package sg.ntuchealth.yoda.edge.exception;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import sg.ntuchealth.yoda.edge.common.StatusCodes;
import sg.ntuchealth.yoda.edge.service.model.LoginResponse;

@RestControllerAdvice
public class ControllerExceptionHandler {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @ExceptionHandler({
    JwkException.class,
    JWTVerificationException.class,
    JWTDecodeException.class,
    SignatureException.class
  })
  public ResponseEntity<LoginResponse> authenticationException(Exception ex) {
    logException(ex);
    return new ResponseEntity<>(
        LoginResponse.builder()
            .success(false)
            .message(StatusCodes.AUTHORIZATION_ERROR.getMessage())
            .statusCode(StatusCodes.AUTHORIZATION_ERROR.getCode())
            .build(),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler({ClientNotFoundException.class, HttpClientErrorException.class})
  public ResponseEntity<LoginResponse> clientNotFoundException(Exception ex) {
    logException(ex);
    return new ResponseEntity<>(
        LoginResponse.builder()
            .success(false)
            .message(StatusCodes.CLIENT_NOT_FOUND.getMessage())
            .statusCode(StatusCodes.CLIENT_NOT_FOUND.getCode())
            .build(),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AssociationNotFoundException.class)
  public ResponseEntity<LoginResponse> associationNotFoundException(Exception ex) {
    logException(ex);
    return new ResponseEntity<>(
        LoginResponse.builder()
            .success(false)
            .message(StatusCodes.ASSOCIATION_NOT_FOUND_IN_TOKEN.getMessage())
            .statusCode(StatusCodes.ASSOCIATION_NOT_FOUND_IN_TOKEN.getCode())
            .build(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TokenExpiredException.class)
  public ResponseEntity<LoginResponse> tokenExpiredException(Exception ex) {
    logException(ex);
    return new ResponseEntity<>(
        LoginResponse.builder()
            .success(false)
            .message(StatusCodes.TOKEN_EXPIRED.getMessage())
            .statusCode(StatusCodes.TOKEN_EXPIRED.getCode())
            .build(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(B3TokenNotFoundException.class)
  public ResponseEntity<LoginResponse> b3TokenNotFoundException(Exception ex) {
    logException(ex);
    return new ResponseEntity<>(
        LoginResponse.builder().success(false).message(ex.getMessage()).build(),
        HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(AssociationNotSavedinLinkIDException.class)
  public ResponseEntity<LoginResponse> associationNotSavedinLindIDException(Exception ex) {
    logException(ex);
    return new ResponseEntity<>(
        LoginResponse.builder()
            .success(false)
            .message(StatusCodes.ASSOCIATION_NOT_SAVED_IN_LINK_ID.getMessage())
            .statusCode(StatusCodes.ASSOCIATION_NOT_SAVED_IN_LINK_ID.getCode())
            .build(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<LoginResponse> genericException(Exception ex) {
    logException(ex);
    return new ResponseEntity<>(
        LoginResponse.builder().success(false).message(ex.getMessage()).build(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private void logException(Throwable t) {
    LOGGER.error(t.getMessage(), t);
  }
}
