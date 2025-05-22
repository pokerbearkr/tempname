package org.example.siljeun.domain.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.siljeun.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long seatScheduleInfoId;
  private String paymentKey;
  private Long amount;

  @Builder
  public Payment(Long seatScheduleInfoId, String paymentKey, Long amount) {
    this.seatScheduleInfoId = seatScheduleInfoId;
    this.paymentKey = paymentKey;
    this.amount = amount;
  }
}
