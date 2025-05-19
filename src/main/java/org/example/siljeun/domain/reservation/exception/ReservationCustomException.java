package org.example.siljeun.domain.reservation.exception;

import lombok.Getter;

public class ReservationCustomException extends RuntimeException {

  @Getter
  private int errorCode;

  public ReservationCustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode.getCode();
  }
}
