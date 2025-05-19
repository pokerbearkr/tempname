package org.example.siljeun.domain.reservation.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  NOT_FOUND_RESERVATION(404, "예매정보가 존재하지 않습니다."),
  INVALID_RESERVATION_USER(400, "예매정보가 일치하지 않습니다.");

  private int code;
  private String message;

  ErrorCode(int errorCode, String message) {
  }

}
