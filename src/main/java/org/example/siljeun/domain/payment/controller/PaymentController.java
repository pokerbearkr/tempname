package org.example.siljeun.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.payment.dto.PaymentConfirmRequestDto;
import org.example.siljeun.domain.payment.service.PaymentService;
import org.example.siljeun.global.security.PrincipalDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

  private final PaymentService paymentService;

  // 결제 위젯 HTML 페이지 반환
  @GetMapping
  public String index() {
    return "redirect:/checkout.html";
  }

  // 결제 성공 콜백 → 결제 승인 처리
  @GetMapping("/success")
  @ResponseBody
  public ResponseEntity<String> sandboxSuccess(@RequestParam String paymentKey,
      @AuthenticationPrincipal PrincipalDetails userDetails,
      @RequestParam Long seatScheduleInfoId,
      @RequestParam Long amount) {
    Long userId = userDetails.getUserId();

    System.out.println("결제 성공 콜백 도착");
    System.out.println("paymentKey: " + paymentKey);
    System.out.println("userId: " + userId);
    System.out.println("seatScheduleInfoId: " + seatScheduleInfoId);
    System.out.println("amount: " + amount);

    PaymentConfirmRequestDto dto = new PaymentConfirmRequestDto(paymentKey, userId,
        seatScheduleInfoId, amount);
    paymentService.savePayment(dto);
    return ResponseEntity.ok("결제정보 저장 완료");
  }


  // 결제 실패 콜백
  @GetMapping("/fail")
  @ResponseBody
  public String sandboxFail(@RequestParam String code,
      @RequestParam String message) {
    return "결제 실패: " + message + " (" + code + ")";
  }
}