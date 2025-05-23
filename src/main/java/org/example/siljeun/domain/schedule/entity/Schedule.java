package org.example.siljeun.domain.schedule.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.siljeun.domain.concert.entity.Concert;
import org.example.siljeun.domain.seatscheduleinfo.entity.SeatScheduleInfo;
import org.example.siljeun.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "schedule")
public class Schedule extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "concert_id", nullable = false)
  private Concert concert;

  @Column(nullable = false)
  private LocalDateTime startTime;

  @Column(nullable = false)
  private LocalDateTime ticketingStartTime;

  public Schedule(Concert concert, LocalDateTime startTime, LocalDateTime ticketingStartTime) {
    this.concert = concert;
    this.startTime = startTime;
    this.ticketingStartTime = ticketingStartTime;
  }

  public void update(LocalDateTime startTime, LocalDateTime ticketingStartTime) {
    this.startTime = startTime;
    this.ticketingStartTime = ticketingStartTime;
  }

}
