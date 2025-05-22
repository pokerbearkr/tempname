package org.example.siljeun.domain.schedule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.schedule.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seat.entity.QSeatScheduleInfo;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.global.lock.DistributedLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatScheduleInfoService {
    private final SeatScheduleInfoRepository seatScheduleInfoRepository;
    private final ScheduleRepository scheduleRepository;

    private final RedisTemplate<String, Long> redisTemplate;

    @Qualifier("redisStatusTemplate")
    private final RedisTemplate<String, String> redisStatusTemplate;

    @DistributedLock(key = "'seat:' + #seatScheduleInfoId")
    public void selectSeat(Long userId, Long seatScheduleInfoId) {

        SeatScheduleInfo seatScheduleInfo = seatScheduleInfoRepository.findById(seatScheduleInfoId).
                orElseThrow(() -> new EntityNotFoundException("해당 회차별 좌석 정보가 존재하지 않습니다."));

        if (!seatScheduleInfo.isAvailable()) {
            //log.info("이미 선점된 좌석입니다.");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 선점된 좌석입니다.");
        }

        seatScheduleInfo.updateSeatScheduleInfoStatus(SeatStatus.SELECTED);
        seatScheduleInfoRepository.save(seatScheduleInfo);

        String redisKey = "seat:" + seatScheduleInfoId;
        redisTemplate.opsForValue().set(redisKey, userId, Duration.ofMinutes(5));

        String redisStatusKey = "seatStatus:" + seatScheduleInfoId;
        redisStatusTemplate.opsForValue().set(redisStatusKey, seatScheduleInfo.getStatus().name(), Duration.ofMinutes(5));
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
            } else if (info.getStatus() == SeatStatus.SELECTED) { //TTL에 의해서 Redis에서는 만료되었으나 DB에 Selected로 저장된 경우
                status = SeatStatus.AVAILABLE.name();
            } else {
                status = info.getStatus().name();
            }

            result.put("seatScheduleInfo-" + info.getId().toString(), status);
        }

        return result;
    }
}
