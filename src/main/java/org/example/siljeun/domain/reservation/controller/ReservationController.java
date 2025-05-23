package org.example.siljeun.domain.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
import org.example.siljeun.domain.reservation.dto.response.ReservationInfoResponse;
import org.example.siljeun.domain.reservation.service.ReservationService;
import org.example.siljeun.global.dto.ResponseDto;
import org.example.siljeun.global.security.PrincipalDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

  private final ReservationService reservationService;

  @PatchMapping("/{reservationId}/discount")
  public ResponseEntity<ResponseDto<Void>> updatePrice(
      @AuthenticationPrincipal PrincipalDetails userDetails,
      @PathVariable Long reservationId,
      @RequestBody @Valid UpdatePriceRequest requestDto) {
    String username = userDetails.getUsername();
    reservationService.updatePrice(username, reservationId, requestDto);
    return ResponseEntity.ok(ResponseDto.success("예매 금액 변경 완료", null));
  }

  @DeleteMapping("/{reservationId}")
  public ResponseEntity<ResponseDto<Void>> delete(
      @AuthenticationPrincipal PrincipalDetails userDetails, @PathVariable Long reservationId) {
    String username = userDetails.getUsername();
    reservationService.delete(username, reservationId);
    return ResponseEntity.ok(ResponseDto.success("예매 취소 완료", null));
  }

  @GetMapping("/{reservationId}")
  public ResponseEntity<ResponseDto<ReservationInfoResponse>> findById(
      @AuthenticationPrincipal PrincipalDetails userDetails, @PathVariable Long reservationId) {
    String username = userDetails.getUsername();
    ReservationInfoResponse dto = reservationService.findById(username, reservationId);
    return ResponseEntity.ok(ResponseDto.success("예매 조회 성공", dto));
  }
}
