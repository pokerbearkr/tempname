package org.example.siljeun.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentConfirmRequestDto {

  @JsonProperty("paymentKey")
  private String paymentKey;

  @JsonProperty("userId")
  private Long userId;

  @JsonProperty("orderId")
  private Long seatScheduleInfoId;

  @JsonProperty("amount")
  private Long amount;
}
