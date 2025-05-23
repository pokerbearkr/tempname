package org.example.siljeun.domain.reservation.scheduler;

import static org.example.siljeun.domain.reservation.service.WaitingQueueService.prefixKeyForSelecingQueue;
import static org.example.siljeun.domain.reservation.service.WaitingQueueService.prefixKeyForWaitingQueue;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckExpiredScheduler {

  private final StringRedisTemplate redisTemplate;
  private final ScheduleRepository scheduleRepository;

  private final Set<String> keys = new HashSet<>();

  // 1시간마다 티켓팅 기간인 schedule을 keys에 저장
  @Scheduled(cron = "0 0 * * * *")
  public void checkOpenedSchedule() {

    keys.clear();

    List<Long> openedSchedules = scheduleRepository.findAllByStartTimeAfterAndTicketingStartTimeBefore(
            LocalDateTime.now(),
            LocalDateTime.now()).stream()
        .map(Schedule::getId)
        .toList();

    try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection()
        .scan(ScanOptions.scanOptions().match(prefixKeyForSelecingQueue + "*")
            .build())) {
      while (cursor.hasNext()) {
        String key = new String(cursor.next(), StandardCharsets.UTF_8);
        String[] parts = key.split(":");
        Long scheduleId = Long.valueOf(parts[2]);

        if (openedSchedules.contains(scheduleId)) {
          keys.add(key);
        }
      }
    }
  }

  // 1분마다 keys에 저장된 각 schedule의 대기열에서 TTL 만료인 유저 삭제
  @Scheduled(cron = "0 * * * * *")
  public void checkExpiredUser() {

    for (String key : keys) {
      redisTemplate.opsForZSet()
          .removeRangeByScore(key, 0, System.currentTimeMillis());
    }
  }

  // 1일마다 예매 종료된 공연은 sorted set에서 삭제
  @Scheduled(cron = "0 0 0 * * *")
  public void deleteExpiredKey() {

    Set<Long> scheduleIdForDelete = new HashSet<>();

    // sorted set에 저장된 scheduleId 추출
    try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection()
        .scan(ScanOptions.scanOptions().match(prefixKeyForSelecingQueue + "*")
            .build())) {
      while (cursor.hasNext()) {
        String key = new String(cursor.next(), StandardCharsets.UTF_8);
        String[] parts = key.split(":");
        Long scheduleId = Long.valueOf(parts[2]);
        scheduleIdForDelete.add(scheduleId);
      }
    }

    // schedule.startTime < 현재 시각인 schedule 추출
    List<Schedule> schedules = scheduleRepository.findByIdInAndStartTimeBefore(
        new ArrayList<>(scheduleIdForDelete),
        LocalDateTime.now()
    );

    // sorted set 에서 제거
    schedules.stream()
        .map(schedule -> prefixKeyForWaitingQueue + schedule.getId())
        .forEach(redisTemplate::delete);
    schedules.stream()
        .map(schedule -> prefixKeyForSelecingQueue + schedule.getId())
        .forEach(redisTemplate::delete);
  }
}
