package org.example.siljeun.domain.reservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // reservation
  NOT_FOUND_RESERVATION(404, "예매정보가 존재하지 않습니다."),
  INVALID_RESERVATION_USER(400, "예매정보가 일치하지 않습니다."),

  // user
  NOT_FOUND_USER(404, "유저정보가 존재하지 않습니다."),

  // schedule
  MISSING_HEADER(400, "필수 헤더값이 누락되었습니다."),

  // jwt
  UNAUTHORIZED(401, "토큰이 유효하지 않습니다."),

  // queue
  QUEUE_INSERT_FAIL(500, "대기열 등록을 실패했습니다.");

  private HttpStatus code;
  private String message;

  ErrorCode(int code, String message) {
  }

}
