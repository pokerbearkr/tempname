package org.example.siljeun.domain.reservation.task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.enums.ReservationStatus;
import org.example.siljeun.domain.reservation.repository.ReservationRepository;
import org.example.siljeun.domain.reservation.service.ReservationService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

  private final ReservationRepository reservationRepository;
  private final ReservationService reservationService;
  private final StringRedisTemplate redisTemplate;

  @Scheduled(fixedRate = 60000) // 1분마다 실행
  public void returnSeat() {
    LocalDateTime now = LocalDateTime.now();

    // Todo : 성능 개선
    reservationRepository.findByStatus(ReservationStatus.PENDING).stream()
        .filter(reservation -> ChronoUnit.MINUTES.between(reservation.getCreatedAt(), now) >= 7)
        .forEach(reservation -> reservationService.delete(reservation.getId()));
  }
}