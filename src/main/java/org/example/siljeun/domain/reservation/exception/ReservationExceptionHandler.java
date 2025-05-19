package org.example.siljeun.domain.reservation.exception;

import org.example.siljeun.global.dto.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<Response<String>> ReservationExceptionHandler(
      ReservationCustomException e) {
    return ResponseEntity.status(e.getErrorCode())
        .body(Response.of(false, e.getMessage()));
  }
}
