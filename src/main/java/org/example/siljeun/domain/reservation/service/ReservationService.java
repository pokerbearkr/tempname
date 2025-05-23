package org.example.siljeun.domain.reservation.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.reservation.dto.request.ReservationCreateRequest;
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
import org.example.siljeun.domain.reservation.dto.response.ReservationInfoResponse;
import org.example.siljeun.domain.reservation.entity.Reservation;
import org.example.siljeun.domain.reservation.exception.CustomException;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.example.siljeun.domain.reservation.repository.ReservationRepository;
import org.example.siljeun.domain.seatscheduleinfo.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seatscheduleinfo.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.domain.user.entity.User;
import org.example.siljeun.domain.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;
  private final WaitingQueueService waitingQueueService;
  private final SeatScheduleInfoRepository seatScheduleInfoRepository;
  private final RedisTemplate<String, String> redisTemplate;


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
    reservation.getSeatScheduleInfo().updateSeatScheduleInfoStatus(SeatStatus.AVAILABLE);
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
