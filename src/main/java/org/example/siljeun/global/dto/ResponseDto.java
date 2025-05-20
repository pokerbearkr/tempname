package org.example.siljeun.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseDto<T> {

  private boolean success;
  private String message;
  @JsonInclude(Include.NON_NULL)
  private T data;

  public static <T> ResponseDto<T> success(String message, T data) {
    return new ResponseDto<>(true, message, data);
  }

  public static <T> ResponseDto<T> fail(String message) {
    return new ResponseDto<>(false, message, null);
  }

}