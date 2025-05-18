package org.example.siljeun.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.entity.Reservation;
import org.example.siljeun.domain.reservation.repository.ReservationRepository;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.example.siljeun.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final ScheduleRepository scheduleRepository;
  private final SeatScheduleInfo seatScheduleInfo;

  // Todo : 예외처리

  public void create(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(RuntimeException::new);
    User user = null; // Todo : User 데이터 DB에 있는지 확인
    Reservation reservation = new Reservation(user, schedule);
    reservationRepository.save(reservation);
  }

  // 좌석 도메인에서 호출할 메서드(해당 도메인에서 reservation Repo에 reservationId가 존재하는지 확인 필요)
  @Transactional
  public void saveSeatInfo(Reservation reservation, SeatScheduleInfo seatScheduleInfo) {
    reservation.updateSeatScheduleInfo(seatScheduleInfo);
  }


}
