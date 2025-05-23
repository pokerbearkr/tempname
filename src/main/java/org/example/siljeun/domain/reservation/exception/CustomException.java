package org.example.siljeun.domain.reservation.exception;

import lombok.Getter;

public class CustomException extends RuntimeException {

  @Getter
  private ErrorCode errorCode;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
