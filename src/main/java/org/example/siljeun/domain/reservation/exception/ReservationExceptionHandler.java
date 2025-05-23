package org.example.siljeun.domain.reservation.exception;

import org.example.siljeun.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ResponseDto<Void>> reservationExceptionHandler(
      CustomException e) {
    return ResponseEntity.status(e.getErrorCode())
        .body(ResponseDto.fail(e.getMessage()));
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, MessageConversionException.class,
      MessagingException.class})
  public ResponseEntity<ResponseDto<Void>> validationExceptionHandler(Exception e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ResponseDto.fail(e.getMessage()));
  }
}
