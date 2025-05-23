package org.example.siljeun.domain.schedule.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.reservation.service.WaitingQueueService;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.schedule.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.global.lock.DistributedLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class SeatScheduleInfoService {

  private final SeatScheduleInfoRepository seatScheduleInfoRepository;
  private final ScheduleRepository scheduleRepository;
  private final RedisTemplate<String, Long> redisSeatUserTemplate;
  private final RedisTemplate<String, String> redisStatusTemplate;
  private final WaitingQueueService waitingQueueService;

  public SeatScheduleInfoService(
      SeatScheduleInfoRepository seatScheduleInfoRepository,
      ScheduleRepository scheduleRepository,
      @Qualifier("redisLongTemplate") RedisTemplate<String, Long> redisSeatUserTemplate,
      @Qualifier("redisStringTemplate") RedisTemplate<String, String> redisStatusTemplate,
      WaitingQueueService waitingQueueService) {
    this.seatScheduleInfoRepository = seatScheduleInfoRepository;
    this.scheduleRepository = scheduleRepository;
    this.redisSeatUserTemplate = redisSeatUserTemplate;
    this.redisStatusTemplate = redisStatusTemplate;
    this.waitingQueueService = waitingQueueService;
  }

  @Transactional
  @DistributedLock(key = "'seat:' + #seatScheduleInfoId")
  public void selectSeat(Long userId, String username, Long seatScheduleInfoId) {

    SeatScheduleInfo seatScheduleInfo = seatScheduleInfoRepository.findById(seatScheduleInfoId).
        orElseThrow(() -> new EntityNotFoundException("해당 회차별 좌석 정보가 존재하지 않습니다."));

    boolean hasPassedQueue = waitingQueueService.hasPassedWaitingQueue(
        seatScheduleInfo.getSchedule().getId(), username);
    if (!hasPassedQueue) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "정상적인 접근이 아닙니다.");
    }

    if (!seatScheduleInfo.isAvailable()) {
      //log.info("이미 선점된 좌석입니다.");
      throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 선점된 좌석입니다.");
    }

    seatScheduleInfo.updateSeatScheduleInfoStatus(SeatStatus.SELECTED);
    seatScheduleInfoRepository.save(seatScheduleInfo);

    String redisKey = "seat:" + seatScheduleInfoId;
    redisSeatUserTemplate.opsForValue().set(redisKey, userId, Duration.ofMinutes(5));

    String redisStatusKey = "seatStatus:" + seatScheduleInfoId;
    redisStatusTemplate.opsForValue()
        .set(redisStatusKey, seatScheduleInfo.getStatus().name(), Duration.ofMinutes(5));

    waitingQueueService.deleteSelectingUser(seatScheduleInfo.getSchedule().getId(), username);
  }

  public Map<String, String> getSeatStatusMap(Long scheduleId) {

    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new EntityNotFoundException("해당 회차가 존재하지 않습니다."));

    List<SeatScheduleInfo> seatScheduleInfos =
        seatScheduleInfoRepository.findAllBySchedule(schedule);

    Map<String, String> result = new HashMap<>();

    for (SeatScheduleInfo info : seatScheduleInfos) {
      String redisKey = "seatStatus:" + info.getId();
      String redisStatus = redisStatusTemplate.opsForValue().get(redisKey);

      String status;
      if (redisStatus != null) {
        status = redisStatus;
      } else if (info.getStatus()
          == SeatStatus.SELECTED) { //TTL에 의해서 Redis에서는 만료되었으나 DB에 Selected로 저장된 경우
        status = SeatStatus.AVAILABLE.name();
      } else {
        status = info.getStatus().name();
      }

      result.put("seatScheduleInfo-" + info.getId().toString(), status);
    }

    return result;
  }
}
