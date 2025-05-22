package org.example.siljeun.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ResponseDto<T> {

  private boolean success;
  private String message;
  private T data;

  public static <T> ResponseDto<T> success(String message, T data) {
    return new ResponseDto<>(true, message, data);
  }

  public static <T> ResponseDto<T> fail(String message) {
    return new ResponseDto<>(false, message, null);
  }

}