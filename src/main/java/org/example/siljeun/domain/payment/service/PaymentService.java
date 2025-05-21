package org.example.siljeun.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.payment.dto.PaymentConfirmRequestDto;
import org.example.siljeun.domain.payment.entity.Payment;
import org.example.siljeun.domain.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;

	public void savePayment(PaymentConfirmRequestDto dto) {
		Payment payment = Payment.builder()
			.paymentKey(dto.getPaymentKey())
			.orderId(dto.getOrderId())
			.amount(dto.getAmount())
			.build();

		paymentRepository.save(payment);
	}
}
