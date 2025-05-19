package org.example.siljeun.domain.reservation.task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.example.siljeun.domain.reservation.enums.ReservationStatus;
import org.example.siljeun.domain.reservation.repository.ReservationRepository;
import org.example.siljeun.domain.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class ReservationScheduler {

  private final ReservationRepository reservationRepository;
  private final ReservationService reservationService;

  @Autowired
  public ReservationScheduler(ReservationRepository reservationRepository,
      ReservationService reservationService) {
    this.reservationRepository = reservationRepository;
    this.reservationService = reservationService;
  }

  @Scheduled(fixedRate = 60000) // 1분마다 실행
  public void returnSeat() {
    LocalDateTime now = LocalDateTime.now();

    reservationRepository.findByStatus(ReservationStatus.PENDING).stream()
        .filter(reservation -> ChronoUnit.MINUTES.between(reservation.getCreated_at(), now) >= 7)
        .forEach(reservation -> reservationService.delete(reservation.getId()));
  }
}