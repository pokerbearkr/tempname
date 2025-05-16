package org.example.siljeun.domain.seat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.seat.enums.SeatReserveStatus;
import org.example.siljeun.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat_status")
public class SeatStatus extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_id", nullable = false)
  private Seat seat;

  //회차 정보
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schedule", nullable = false)
  private Schedule schedule;

  //상태 (확장성을 위해 우선 enum으로 설정해놓음)
  private SeatReserveStatus status;

  //임시로 String으로 설정
  private String grade;

  private int price;

}
