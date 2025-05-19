package org.example.siljeun.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
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

  // Todo : 예외처리

  public void create(Long scheduleId) {
    User user = null; // Todo : User 데이터 DB에 있는지 확인
    Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(RuntimeException::new);
    // Todo : 티켓팅 가능 시간인지 확인
    Reservation reservation = new Reservation(user, schedule);
    reservationRepository.save(reservation);
  }

  // 좌석 도메인에서 호출할 메서드 - 예매 테이블에 좌석 정보 저장
  @Transactional
  public void saveSeatInfo(Reservation reservation, SeatScheduleInfo seatScheduleInfo) {
    reservation.saveSeatScheduleInfo(seatScheduleInfo);
  }

  // 결제 도메인에서 호출할 메서드 - 결제완료 처리
  @Transactional
  public void updateReservationStatus(Reservation reservation) {
    reservation.updateReservationStatus();
  }

  @Transactional
  public void updatePrice(Long reservationId, UpdatePriceRequest requestDto) {
    User user = null; // Todo : User 데이터 DB에 있는지 확인
    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
        RuntimeException::new);

    if (reservation.getUser() != user) {
      throw new RuntimeException();
    }

    reservation.updateTicketPrice(requestDto);
  }

  @Transactional
  public void delete(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
        RuntimeException::new);

    reservationRepository.delete(reservation);
    // Todo : seatScheduleInfo 테이블에 해당 좌석 선택가능으로 변경
  }
}
