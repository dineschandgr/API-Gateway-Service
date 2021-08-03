package sg.ntuchealth.yoda.edge.filter.exceptions;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import sg.ntuchealth.yoda.edge.common.StatusCodes;

@Component
@Getter
@Setter
public class GlobalErrorAttributes extends DefaultErrorAttributes {

  public static final String STATUS_CODE = "statusCode";
  private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Override
  public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
    Map<String, Object> map = super.getErrorAttributes(request, includeStackTrace);

    Exception ex = (Exception) getError(request);
    logException(ex);

    if (ex instanceof TokenExpiredGlobalException) {
      map.put(STATUS_CODE, StatusCodes.TOKEN_EXPIRED.getCode());
    } else if (ex instanceof AssocationNotFoundGlobalException) {
      map.put(STATUS_CODE, StatusCodes.ASSOCIATION_NOT_FOUND_IN_TOKEN.getCode());
    } else if (ex instanceof AuthorizationGlobalException) {
      map.put(STATUS_CODE, StatusCodes.AUTHORIZATION_ERROR.getCode());
    }

    map.remove("path");
    map.remove("requestId");
    map.remove("status");
    return map;
  }

  private void logException(Throwable t) {
    LOGGER.error(t.getMessage(), t);
  }
}
