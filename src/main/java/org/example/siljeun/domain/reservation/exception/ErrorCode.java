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
  NOT_FOUND_SCHEDULE(404, "공연 회차 정보가 존재하지 않습니다."),
  MISSING_HEADER(400, "필수 헤더값이 누락되었습니다."),

  // seatScheduleInfo
  NOT_FOUNT_SEAT_SCHEDULE_INFO(404, "해당 공연에 대한 좌석 정보가 존재하지 않습니다."),
  ALREADY_SELECTED_SEAT(409, "이미 선점된 좌석입니다."),
  SEAT_LIMIT_ONE_PER_USER(409, "1인당 1개의 좌석만 예약 가능합니다."),

  // venue
  NOT_FOUND_VENUE(404, "해당 공연장을 찾을 수 없습니다."),

  // seat
  SEAT_CAPACITY_EXCEEDED(400, "좌석 수가 공연장 수용 인원(capacity)을 초과했습니다."),

  // jwt
  UNAUTHORIZED(401, "토큰이 유효하지 않습니다."),

  // queue
  QUEUE_INSERT_FAIL(500, "대기열 등록을 실패했습니다."),
  NOT_TICKETING_TIME(400, "예매 가능 시간이 아닙니다.");

  private HttpStatus code;
  private String message;

  ErrorCode(int code, String message) {
  }

}
