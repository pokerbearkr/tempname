package org.example.siljeun.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentConfirmRequestDto {

	@JsonProperty("paymentKey")
	private String paymentKey;

	@JsonProperty("orderId")
	private String orderId;

	@JsonProperty("amount")
	private Long amount;
}
