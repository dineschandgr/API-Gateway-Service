package sg.ntuchealth.yoda.edge.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.netty.buffer.ByteBufAllocator;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class XSSFilter implements WebFilter {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Override
  public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

    ServerHttpRequest request = serverWebExchange.getRequest();

    URI uri = request.getURI();
    LOGGER.debug("URI: " + uri.toString());
    String rawPath = uri.getRawPath();
    if (!StringUtils.isEmpty(rawPath)) {
      rawPath = stripXSS(rawPath);
    }
    String rawQuery = uri.getRawQuery();
    if (!StringUtils.isEmpty(rawQuery)) {
      rawQuery = getStrippedRawQuery(rawQuery);
    }

    URI newUri;
    try {
      newUri =
          UriComponentsBuilder.fromUri(uri)
              .replaceQuery(rawQuery)
              .replacePath(rawPath)
              .encode()
              .build()
              .toUri();
      LOGGER.debug("New URI: " + newUri.toString());
    } catch (Exception e) {
      LOGGER.error("Invalid URI: ", e);
      throw new IllegalStateException("Invalid URI: " + uri.toString());
    }

    if (uri.compareTo(newUri) == 0) {
      return webFilterChain.filter(serverWebExchange);
    } else {
      ServerHttpRequest newRequest = request.mutate().uri(newUri).build();
      return webFilterChain.filter(serverWebExchange.mutate().request(newRequest).build());
    }
  }

  private void loggingRequest(ServerHttpRequest request) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    String jsonInString = mapper.writeValueAsString(request);
    LOGGER.debug(jsonInString);
    LOGGER.debug("path -- " + request.getPath());
  }

  private String getStrippedRawQuery(String rawQuery) {
    List<String> values = Arrays.asList(rawQuery.split("&"));
    if (values.isEmpty()) {
      return rawQuery;
    }
    return values.stream().map(this::stripXSS).collect(Collectors.joining("&"));
  }

  private Flux<DataBuffer> getStrippedRequestBody(ServerHttpRequest request) {
    return request
        .getBody()
        .flatMap(
            buffer -> {
              CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
              DataBufferUtils.release(buffer);
              return Flux.just(stringBuffer(stripXSS(charBuffer.toString())));
            });
  }

  private DataBuffer stringBuffer(String value) {
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    NettyDataBufferFactory nettyDataBufferFactory =
        new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
    DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
    buffer.write(bytes);
    return buffer;
  }

  private String stripXSS(String value) {
    if (value == null) {
      return null;
    }
    try {
      value = ESAPI.encoder().canonicalize(value).replaceAll("\0", "");
      return Jsoup.clean(value, Whitelist.none());
    } catch (Exception e) {
      LOGGER.error("Error while stripping XSS: ", e);
      throw new IllegalStateException("Error while stripping XSS: " + value);
    }
  }
}
