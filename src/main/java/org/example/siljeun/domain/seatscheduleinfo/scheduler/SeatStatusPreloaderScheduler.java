package org.example.siljeun.domain.seatscheduleinfo.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.seatscheduleinfo.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seatscheduleinfo.entity.SeatScheduleInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatStatusPreloaderScheduler {
    private final ScheduleRepository scheduleRepository;
    private final SeatScheduleInfoRepository seatScheduleInfoRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedRate = 60_000)
    public void loadSeatStatusToRedis() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesLater = now.plusMinutes(5);

        List<Schedule> openedSchedules = scheduleRepository.findAllByTicketingStartTimeBetween(now, fiveMinutesLater);

        for (Schedule schedule : openedSchedules) {
            List<SeatScheduleInfo> seatScheduleInfos = seatScheduleInfoRepository.findAllBySchedule(schedule);

            String key = "seatStatus:" +  schedule.getId();
            Map<String, String> seatStatusMap = new HashMap<>();

            for (SeatScheduleInfo seat : seatScheduleInfos) {
                seatStatusMap.put(seat.getId().toString(), seat.getStatus().name());
            }

            redisTemplate.opsForHash().putAll(key, seatStatusMap);
            log.info("✅ scheduleId: {}의 seatSchedulerInfos 적재 완료", schedule.getId());
        }
    }
}
