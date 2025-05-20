package org.example.siljeun.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
import org.example.siljeun.domain.reservation.entity.Reservation;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.example.siljeun.domain.reservation.exception.CustomException;
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
  private final WaitingQueueService waitingQueueService;

  // 좌석 도메인에서 호출할 메서드 - 예매 정보 저장
  @Transactional
  public void save(User user, SeatScheduleInfo seatScheduleInfo) {
    Reservation reservation = new Reservation(user, seatScheduleInfo);
    reservationRepository.save(reservation);
    waitingQueueService.deleteAtQueue(seatScheduleInfo.getSchedule().getId(), user.getUsername());
  }

  // 결제 도메인에서 호출할 메서드 - 결제완료 or 결제취소 처리
  @Transactional
  public void updateReservationStatus(Reservation reservation) {
    reservation.updateReservationStatus(reservation);
  }

  @Transactional
  public void updatePrice(String username, Long reservationId, UpdatePriceRequest requestDto) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION));

    if (reservation.getUser() != user) {
      throw new CustomException(ErrorCode.INVALID_RESERVATION_USER);
    }

    reservation.updateTicketPrice(requestDto);
  }

  @Transactional
  public void delete(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
        () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION));

    reservationRepository.delete(reservation);
    // Todo : 좌석 상태 변경
  }
}
