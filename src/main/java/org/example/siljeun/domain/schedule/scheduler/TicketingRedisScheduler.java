package org.example.siljeun.domain.schedule.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.schedule.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TicketingRedisScheduler {
    private final ScheduleRepository scheduleRepository;
    private final SeatScheduleInfoRepository seatScheduleInfoRepository;
    private final RedisTemplate<String, String> redisStatusTemplate;

    public TicketingRedisScheduler(
            ScheduleRepository scheduleRepository,
            SeatScheduleInfoRepository seatScheduleInfoRepository,
            @Qualifier("redisStringTemplate") RedisTemplate<String, String> redisStatusTemplate
    ){
        this.scheduleRepository = scheduleRepository;
        this.seatScheduleInfoRepository = seatScheduleInfoRepository;
        this.redisStatusTemplate = redisStatusTemplate;
    }

    @Scheduled(fixedRate = 60_000)
    public void loadSeatStatusToRedis() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesLater = now.plusMinutes(5); //티켓팅 시작 시간이 임박한 회차에 대해 미리 Redis에 정보 적재

        List<Schedule> openedSchedules = scheduleRepository.findAllByTicketingStartTimeBetween(now, fiveMinutesLater);

        for (Schedule schedule : openedSchedules) {
            List<SeatScheduleInfo> seatScheduleInfos = seatScheduleInfoRepository.findAllBySchedule(schedule);

            for(SeatScheduleInfo seatScheduleInfo : seatScheduleInfos){
                String key = "seatStatus:" +  seatScheduleInfo.getId().toString();
                String value = seatScheduleInfo.getStatus().name();
                redisStatusTemplate.opsForValue().set(key, value);
            }
        }
    }
}
