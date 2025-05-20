package org.example.siljeun.domain.reservation.exception;

import org.example.siljeun.global.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ResponseDto<String>> reservationExceptionHandler(
      CustomException e) {
    return ResponseEntity.status(e.getErrorCode())
        .body(ResponseDto.fail(e.getMessage()));
  }
}
