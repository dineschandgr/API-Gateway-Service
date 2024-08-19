package io.microservices.apigw.configuration;

public class UserContext {

  private static final ThreadLocal<Long> CONTEXT = new ThreadLocal<>();

  public static void setUserId(Long userId) {
    CONTEXT.set(userId);
  }

  public static Long getUserId() {
    return CONTEXT.get();
  }

}
