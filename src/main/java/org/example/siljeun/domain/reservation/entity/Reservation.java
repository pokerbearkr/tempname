package org.example.siljeun.domain.reservation.entity;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
import org.example.siljeun.domain.reservation.enums.Discount;
import org.example.siljeun.domain.reservation.enums.ReservationStatus;
import org.example.siljeun.domain.reservation.enums.TicketReceipt;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.example.siljeun.domain.user.entity.User;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "reservation")
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schedule_id", nullable = false)
  private Schedule schedule;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_schedule_info_id")
  private SeatScheduleInfo seatScheduleInfo;

  private int price;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TicketReceipt ticketReceipt;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Discount discount;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime created_at;

  public Reservation(User user, Schedule schedule) {
    this.user = user;
    this.schedule = schedule;
    this.ticketReceipt = TicketReceipt.PICKUP;
    this.discount = Discount.GENERAL;
    this.status = ReservationStatus.PENDING;
  }

  public void saveSeatScheduleInfo(SeatScheduleInfo seatScheduleInfo) {
    this.seatScheduleInfo = seatScheduleInfo;
    this.price = seatScheduleInfo.getPrice();
  }

  public void updateReservationStatus() {
    this.status = ReservationStatus.COMPLETE;
  }

  public void updateTicketPrice(UpdatePriceRequest dto) {
    if (!StringUtils.isBlank(dto.ticketReceipt())) {
      this.ticketReceipt = TicketReceipt.valueOf(dto.ticketReceipt());
    }
    if (!StringUtils.isBlank(dto.discount())) {
      this.discount = Discount.valueOf(dto.discount());
    }
  }
}
