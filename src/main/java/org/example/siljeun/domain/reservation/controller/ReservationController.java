package org.example.siljeun.domain.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
import org.example.siljeun.domain.reservation.service.ReservationService;
import org.example.siljeun.global.dto.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PatchMapping("/reservations/{reservationId}/discount")
  public ResponseEntity<Response<String>> updatePrice(@RequestAttribute Long userId,
      @PathVariable Long reservationId,
      @RequestBody UpdatePriceRequest requestDto) {
    reservationService.updatePrice(userId, reservationId, requestDto);
    return ResponseEntity.ok(Response.from("예매 금액 변경 완료"));
  }
}
