package sg.ntuchealth.yoda.edge.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public final class CommonUtils {

  public static HttpHeaders getJsonRequestResponseHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
}
