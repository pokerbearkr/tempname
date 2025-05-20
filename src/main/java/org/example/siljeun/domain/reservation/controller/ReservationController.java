package org.example.siljeun.domain.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
import org.example.siljeun.domain.reservation.service.ReservationService;
import org.example.siljeun.global.dto.ResponseDto;
import org.example.siljeun.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PatchMapping("/reservations/{reservationId}/discount")
  public ResponseEntity<ResponseDto<String>> updatePrice(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long reservationId,
      @RequestBody UpdatePriceRequest requestDto) {
    String username = userDetails.getUsername();
    reservationService.updatePrice(username, reservationId, requestDto);
    return ResponseEntity.ok(ResponseDto.success("예매 금액 변경 완료", null));
  }
}
