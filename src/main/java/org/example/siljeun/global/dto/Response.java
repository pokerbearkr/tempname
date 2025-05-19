package org.example.siljeun.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record Response<T>(

    boolean success,
    String message,
    @JsonInclude(Include.NON_NULL)
    T data
) {

  public static <T> Response<T> from(String message) {
    return new Response<>(true, message, null);
  }

  public static <T> Response<T> of(boolean success, String message) {
    return new Response<>(success, message, null);
  }
}
