package org.example.siljeun.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
import org.example.siljeun.domain.reservation.dto.response.ReservationInfoResponse;
import org.example.siljeun.domain.reservation.entity.Reservation;
import org.example.siljeun.domain.reservation.exception.CustomException;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.example.siljeun.domain.reservation.repository.ReservationRepository;
import org.example.siljeun.domain.schedule.repository.SeatScheduleInfoRepository;
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
  private final SeatScheduleInfoRepository seatScheduleInfoRepository;

  @Transactional
  public void save(Long userId, Long seatScheduleInfoId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    SeatScheduleInfo seatScheduleInfo = seatScheduleInfoRepository.findById(seatScheduleInfoId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUNT_SEAT_SCHEDULE_INFO));

    Reservation reservation = new Reservation(user, seatScheduleInfo);
    reservationRepository.save(reservation);
    waitingQueueService.deleteSelectingUser(seatScheduleInfo.getSchedule().getId(),
        user.getUsername());
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
  public void delete(String username, Long reservationId) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
        () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION));

    if (reservation.getUser() != user) {
      throw new CustomException(ErrorCode.INVALID_RESERVATION_USER);
    }

    reservationRepository.delete(reservation);
    // Todo : 좌석 상태 변경
  }

  public ReservationInfoResponse findById(String username, Long reservationId) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
        () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION));

    if (reservation.getUser() != user) {
      throw new CustomException(ErrorCode.INVALID_RESERVATION_USER);
    }

    return ReservationInfoResponse.from(reservation);
  }
}
