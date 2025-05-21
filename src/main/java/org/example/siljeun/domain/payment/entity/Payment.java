package org.example.siljeun.domain.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.example.siljeun.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderId;
	private String paymentKey;
	private Long amount;

	@Builder
	public Payment(String orderId, String paymentKey, Long amount) {
		this.orderId = orderId;
		this.paymentKey = paymentKey;
		this.amount = amount;
	}
}
