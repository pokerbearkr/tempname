package org.example.siljeun.domain.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.siljeun.domain.seat.entity.SeatStatus;
import org.example.siljeun.domain.user.entity.User;
import org.springframework.data.annotation.CreatedDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_status_id", nullable = false)
  private SeatStatus seatStatus;

  @Column(nullable = false)
  private int price;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private DeliveryFee deliveryFee;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private DiscountRate discountRate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime created_at;

  public enum DeliveryFee {
    DELIVERY, PICKUP
  }

  public enum DiscountRate {
    GENERAL, SEVERELY, MILDLY, NATIONAL_MERIT // 일반, 중증, 경증, 국가유공자
  }

  public enum ReservationStatus {
    PENDING, COMPLETE
  }
}
