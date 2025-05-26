package org.example.siljeun.domain.reservation.enums;

public enum Discount {
  GENERAL(0.0), SEVERELY(0.3), MILDLY(0.3), NATIONAL_MERIT(0.3); // 일반, 중증, 경증, 국가유공자

  Discount(double discountRate) {
  }
}
