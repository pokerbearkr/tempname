package org.example.siljeun.domain.seatscheduleinfo.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.reservation.exception.CustomException;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.example.siljeun.domain.reservation.service.WaitingQueueService;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.domain.seatscheduleinfo.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seatscheduleinfo.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.user.entity.User;
import org.example.siljeun.domain.user.repository.UserRepository;
import org.example.siljeun.global.lock.DistributedLock;
import org.example.siljeun.global.util.RedisKeyProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatScheduleInfoService {

  private final SeatScheduleInfoRepository seatScheduleInfoRepository;
  private final ScheduleRepository scheduleRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final WaitingQueueService waitingQueueService;
  private final UserRepository userRepository;

  @DistributedLock(key = "'seat:' + #seatScheduleInfoId")
  public void selectSeat(Long userId, Long scheduleId, Long seatScheduleInfoId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

    //대기열을 거쳐서 요청했는지 검증 (정상적인 요청인지 검증)
    boolean hasPassed = waitingQueueService.hasPassedWaitingQueue(scheduleId, user.getUsername());
    if (!hasPassed) {
      throw new CustomException(ErrorCode.PRECONDITION_REQUIRED);
    }

    //예외 상황 처리
    SeatScheduleInfo seatScheduleInfo = seatScheduleInfoRepository.findById(seatScheduleInfoId).
        orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUNT_SEAT_SCHEDULE_INFO));

    Schedule schedule = seatScheduleInfo.getSchedule();
    if (schedule.getTicketingStartTime().isAfter(LocalDateTime.now())) {
      throw new CustomException(ErrorCode.NOT_TICKETING_TIME);
    }

    if (!seatScheduleInfo.isAvailable()) {
      throw new CustomException(ErrorCode.ALREADY_SELECTED_SEAT);
    }

    String redisSelectedKey = RedisKeyProvider.userSelectedSeatKey(userId, scheduleId);
    if (Boolean.TRUE.equals(redisTemplate.hasKey(redisSelectedKey))) {
      throw new CustomException(ErrorCode.SEAT_LIMIT_ONE_PER_USER);
    }

    //DB 상태 변경
    seatScheduleInfo.updateSeatScheduleInfoStatus(SeatStatus.SELECTED);
    seatScheduleInfoRepository.save(seatScheduleInfo);

    //유저가 선점한 좌석을 Redis에 저장 (정보 조회용)
    redisTemplate.opsForValue()
        .set(redisSelectedKey, seatScheduleInfoId.toString());
    redisTemplate.expire(redisSelectedKey, Duration.ofMinutes(5));

    //TTL 관리를 위한 키 생성
    String redisLockKey = RedisKeyProvider.seatOccupyKey(seatScheduleInfoId);
    redisTemplate.opsForValue().set(redisLockKey, userId.toString());

    //Redis 상태 변경
    updateSeatScheduleInfoStatusInRedis(scheduleId, seatScheduleInfoId, SeatStatus.SELECTED);

    //TTL 적용
    applySeatLockTTL(seatScheduleInfoId, SeatStatus.SELECTED);

    //좌석 선택중인 유저 queue에서 데이터 삭제
    waitingQueueService.addSelectingQueue(scheduleId, user.getUsername());
  }

  public Map<String, String> getSeatStatusMap(Long scheduleId) {

    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SCHEDULE));

    List<SeatScheduleInfo> seatScheduleInfos = seatScheduleInfoRepository.findAllBySchedule(
        schedule);
    if (seatScheduleInfos.isEmpty()) {
      throw new CustomException(ErrorCode.NOT_FOUNT_SEAT_SCHEDULE_INFO);
    }

    List<String> fieldKeys = seatScheduleInfos.stream()
        .map(info -> info.getId().toString())
        .toList();

    String redisKey = RedisKeyProvider.seatStatusKey(scheduleId);
    List<Object> redisStatuses = redisTemplate.opsForHash()
        .multiGet(redisKey, new ArrayList<>(fieldKeys));

    Map<String, String> seatStatusMap = new HashMap<>();
    for (int i = 0; i < seatScheduleInfos.size(); i++) {
      SeatScheduleInfo info = seatScheduleInfos.get(i);
      Object redisStatusObj = redisStatuses.get(i);

      String status = redisStatusObj != null
          ? redisStatusObj.toString()
          : seatScheduleInfos.get(i).getStatus().name();

      seatStatusMap.put("seatScheduleInfo-" + info.getId().toString(), status);
    }

    return seatStatusMap;
  }

  public void forceSeatScheduleInfoInRedis(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SCHEDULE));

    List<SeatScheduleInfo> seatInfos = seatScheduleInfoRepository.findAllBySchedule(schedule);

    String redisHashKey = RedisKeyProvider.seatStatusKey(scheduleId);
    Map<String, String> seatStatusMap = new HashMap<>();

    for (SeatScheduleInfo seat : seatInfos) {
      seatStatusMap.put(seat.getId().toString(), seat.getStatus().name());
    }

    redisTemplate.opsForHash().putAll(redisHashKey, seatStatusMap);
  }

  @Transactional
  public void updateSeatScheduleInfoStatus(Long seatScheduleInfoId, SeatStatus seatStatus) {
    SeatScheduleInfo seatScheduleInfo = seatScheduleInfoRepository.findById(seatScheduleInfoId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUNT_SEAT_SCHEDULE_INFO));
    seatScheduleInfo.updateSeatScheduleInfoStatus(seatStatus);

    Long scheduleId = seatScheduleInfo.getSchedule().getId();
    updateSeatScheduleInfoStatusInRedis(scheduleId, seatScheduleInfoId, seatStatus);
  }

  public void updateSeatScheduleInfoStatusInRedis(Long scheduleId, Long seatScheduleInfoId,
      SeatStatus seatStatus) {
    String redisKey = RedisKeyProvider.seatStatusKey(scheduleId);
    String fieldKey = seatScheduleInfoId.toString();
    redisTemplate.opsForHash().put(redisKey, fieldKey, seatStatus);
  }

  public void applySeatLockTTL(Long seatScheduleInfoId, SeatStatus seatStatus) {
    String member = seatScheduleInfoId.toString();

    String seatLockkey = RedisKeyProvider.seatOccupyKey(seatScheduleInfoId);
    String zsetSelectedKey = RedisKeyProvider.trackExpiresKey(SeatStatus.SELECTED.name());
    String zsetHoldKey = RedisKeyProvider.trackExpiresKey(SeatStatus.HOLD.name());

    Duration ttl = null;
    long nowMillis = System.currentTimeMillis();

    redisTemplate.opsForZSet().remove(zsetSelectedKey, member);
    redisTemplate.opsForZSet().remove(zsetHoldKey, member);

    switch (seatStatus) {
      case SELECTED:
        ttl = Duration.ofMinutes(5);
        redisTemplate.expire(seatLockkey, ttl);
        redisTemplate.opsForZSet().add(zsetSelectedKey, member, nowMillis + ttl.toMillis());
        break;
      case HOLD:
        ttl = Duration.ofMinutes(60);
        redisTemplate.expire(seatLockkey, ttl);
        redisTemplate.opsForZSet().add(zsetHoldKey, member, nowMillis + ttl.toMillis());
        break;
      default:
        redisTemplate.persist(seatLockkey);
        break;
    }
  }

  public SeatScheduleInfo findById(Long seatScheduleInfoId) {
    return seatScheduleInfoRepository.findById(seatScheduleInfoId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUNT_SEAT_SCHEDULE_INFO));
  }
}
