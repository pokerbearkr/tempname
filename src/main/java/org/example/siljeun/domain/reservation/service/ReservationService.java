package org.example.siljeun.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
import org.example.siljeun.domain.reservation.entity.Reservation;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.example.siljeun.domain.reservation.exception.ReservationCustomException;
import org.example.siljeun.domain.reservation.repository.ReservationRepository;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.example.siljeun.domain.user.entity.User;
import org.example.siljeun.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;

  // 좌석 도메인에서 호출할 메서드 - 예매 정보 저장
  @Transactional
  public void save(User user, SeatScheduleInfo seatScheduleInfo) {
    Reservation reservation = new Reservation(user, seatScheduleInfo);
    reservationRepository.save(reservation);
  }

  // 결제 도메인에서 호출할 메서드 - 결제완료 처리
  @Transactional
  public void updateReservationStatus(Reservation reservation) {
    reservation.updateReservationStatus();
  }

  @Transactional
  public void updatePrice(Long userId, Long reservationId, UpdatePriceRequest requestDto) {
    User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
        () -> new ReservationCustomException(ErrorCode.NOT_FOUND_RESERVATION));

    if (reservation.getUser() != user) {
      throw new ReservationCustomException(ErrorCode.INVALID_RESERVATION_USER);
    }

    reservation.updateTicketPrice(requestDto);
  }

  @Transactional
  public void delete(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
        () -> new ReservationCustomException(ErrorCode.NOT_FOUND_RESERVATION));

    reservationRepository.delete(reservation);
    // 좌석 상태 변경
  }
}
