package sg.ntuchealth.yoda.edge.exception;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import sg.ntuchealth.yoda.edge.service.model.LoginResponse;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler({HttpClientErrorException.class, ServerWebInputException.class})
    public Mono<ResponseEntity<LoginResponse>> httpClientErrorException(Exception ex) {
        logException(ex);
        return Mono.just(new ResponseEntity(
                LoginResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler({ClientNotFoundException.class, JwkException.class, JWTVerificationException.class, JWTDecodeException.class})
    public Mono<ResponseEntity<LoginResponse>> authenticationException(Exception ex) {
        logger.error("ExceptionHandler ++++ß");
        logException(ex);
        return Mono.just(new ResponseEntity(
                LoginResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build(),
                HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<LoginResponse>> Exception(Exception ex) {
        logException(ex);
        return Mono.just(new ResponseEntity(
                LoginResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private void logException(Throwable t) {
        logger.error(t.getMessage(), t);
    }

}