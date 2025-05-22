package org.example.siljeun.domain.reservation.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
import org.example.siljeun.domain.reservation.dto.response.ReservationInfoResponse;
import org.example.siljeun.domain.reservation.entity.Reservation;
import org.example.siljeun.domain.reservation.exception.CustomException;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.example.siljeun.domain.reservation.repository.ReservationRepository;
import org.example.siljeun.domain.seatscheduleinfo.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.domain.user.entity.User;
import org.example.siljeun.domain.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

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
    waitingQueueService.deleteAtQueue(seatScheduleInfo.getSchedule().getId(), user.getUsername());
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

  @Transactional
  public void createReservation(Long scheduleId, Long userId){

    //유저 확인
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

    //유저가 해당 회차에 선택한 좌석 검증
    String redisSelectedKey = "user:scheduleSelected:" + userId + ":" + scheduleId;
    String selectedId = redisTemplate.opsForValue().get(redisSelectedKey);

    if (selectedId == null) {
      throw new IllegalStateException("선택한 좌석이 없습니다.");
    }

    SeatScheduleInfo seatScheduleInfo = seatScheduleInfoRepository.findById(Long.valueOf(selectedId))
            .orElseThrow(() -> new EntityNotFoundException("좌석 정보를 찾을 수 없습니다."));

    //해당 좌석의 상태 검증
    String redisStatusHashKey = "seatStatus:" + scheduleId;
    Object redisStatusObj = redisTemplate.opsForHash().get(redisStatusHashKey, selectedId);

    if (redisStatusObj == null || !redisStatusObj.toString().equals(SeatStatus.SELECTED.name())) {
      throw new IllegalStateException("좌석 상태가 유효하지 않습니다. 다시 선택해주세요.");
    }

    //예매 정보 생성
    Reservation reservation = new Reservation(user, seatScheduleInfo);
    reservationRepository.save(reservation);

    //좌석 상태 결제 진행 중으로 변경
    seatScheduleInfo.updateSeatScheduleInfoStatus(SeatStatus.HOLD);
    seatScheduleInfoRepository.save(seatScheduleInfo);
    redisTemplate.opsForHash().put(redisStatusHashKey, selectedId, SeatStatus.HOLD.name());

    //유저가 선점한 좌석 정보 - 결제 진행 상태일 때의 만료 시간 1시간
    redisTemplate.expire(redisSelectedKey, Duration.ofMinutes(60));
  }
}
