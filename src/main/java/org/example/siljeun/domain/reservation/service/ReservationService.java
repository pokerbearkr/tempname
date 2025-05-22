package org.example.siljeun.domain.reservation.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.request.UpdatePriceRequest;
import org.example.siljeun.domain.reservation.dto.response.ReservationInfoResponse;
import org.example.siljeun.domain.reservation.entity.Reservation;
import org.example.siljeun.domain.reservation.exception.CustomException;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.example.siljeun.domain.reservation.repository.ReservationRepository;
import org.example.siljeun.domain.schedule.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.domain.user.entity.User;
import org.example.siljeun.domain.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    Set<String> seatScheduleIds = redisTemplate.opsForSet().members(redisSelectedKey);

    if (seatScheduleIds == null || seatScheduleIds.isEmpty()) {
      throw new IllegalStateException("선택한 좌석이 없습니다.");
    }

    List<SeatScheduleInfo> seatsScheduleInfos = seatScheduleInfoRepository.findAllById(
            seatScheduleIds.stream().map(Long::valueOf).collect(Collectors.toList())
    );

    for (SeatScheduleInfo seatScheduleInfo : seatsScheduleInfos) {
      String key = "seatStatus:" + seatScheduleInfo.getId();
      String status = redisTemplate.opsForValue().get(key);
      if (!"SELECTED".equals(status)) {
        throw new IllegalStateException("좌석 상태가 유효하지 않습니다. 다시 선택해주세요.");
      }
    }

    //검증 끝난 데이터에 대해 예매 정보 생성 및 상태 업데이트
    for (SeatScheduleInfo seatScheduleInfo : seatsScheduleInfos) {
      Reservation reservation = new Reservation(user, seatScheduleInfo);
      reservationRepository.save(reservation);

      seatScheduleInfo.updateSeatScheduleInfoStatus(SeatStatus.HOLD); //결제 진행 중!
      seatScheduleInfoRepository.save(seatScheduleInfo);

      String redisSeatKey = "seatStatus:" + seatScheduleInfo.getId();
      redisTemplate.opsForValue().set(redisSeatKey, SeatStatus.HOLD.name(), Duration.ofMinutes(60));
    }
  }
}
