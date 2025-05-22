package org.example.siljeun.domain.schedule.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.schedule.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketingRedisScheduler {
    private final ScheduleRepository scheduleRepository;
    private final SeatScheduleInfoRepository seatScheduleInfoRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    @Scheduled(fixedRate = 60_000)
    public void loadSeatStatusToRedis() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesLater = now.plusMinutes(5); //티켓팅 시작 시간이 임박한 회차에 대해 미리 Redis에 정보 적재

        List<Schedule> openedSchedules = scheduleRepository.findAllByTicketingStartTimeBetween(now, fiveMinutesLater);

        for (Schedule schedule : openedSchedules) {
            Long scheduleId = schedule.getId();
            String redisKey = "seatStatus:" + scheduleId;

            List<SeatScheduleInfo> seatScheduleInfos = seatScheduleInfoRepository.findAllBySchedule(schedule);

            Map<String, String> seatStatusMap = seatScheduleInfos.stream()
                    .collect(Collectors.toMap(
                            seat -> String.valueOf(seat.getId()),
                            seat -> seat.getStatus().name()
                    ));

            redisTemplate.opsForHash().putAll(redisKey, seatStatusMap);
            log.info("✅ Redis에 좌석 상태 저장 완료 [key: {}] seatCount: {}", redisKey, seatStatusMap.size());
        }
    }

}
