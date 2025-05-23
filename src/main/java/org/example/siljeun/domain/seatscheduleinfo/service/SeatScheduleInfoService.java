package org.example.siljeun.domain.seatscheduleinfo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.seat.entity.Seat;
import org.example.siljeun.domain.seat.repository.SeatRepository;
import org.example.siljeun.domain.seatscheduleinfo.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seatscheduleinfo.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.global.lock.DistributedLock;
import org.example.siljeun.global.util.RedisKeyProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatScheduleInfoService {
    private final SeatScheduleInfoRepository seatScheduleInfoRepository;
    private final ScheduleRepository scheduleRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @DistributedLock(key = "'seat:' + #seatScheduleInfoId")
    public void selectSeat(Long userId, Long scheduleId, Long seatScheduleInfoId) {

        SeatScheduleInfo seatScheduleInfo = seatScheduleInfoRepository.findById(seatScheduleInfoId).
                orElseThrow(() -> new EntityNotFoundException("해당 회차별 좌석 정보가 존재하지 않습니다."));

        Schedule schedule = seatScheduleInfo.getSchedule();

        if(schedule.getTicketingStartTime().isAfter(LocalDateTime.now())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "예매 불가능한 시간입니다. 예매 오픈 시간 : " + schedule.getTicketingStartTime());
        }

        if (!seatScheduleInfo.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 선점된 좌석입니다.");
        }

        seatScheduleInfo.updateSeatScheduleInfoStatus(SeatStatus.SELECTED);
        seatScheduleInfoRepository.save(seatScheduleInfo);

        String redisSelectedKey = RedisKeyProvider.userSelectedSeatKey(userId, scheduleId);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisSelectedKey))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "1인당 1개의 좌석만 예약 가능합니다.");
        }

        redisTemplate.opsForValue().set(redisSelectedKey, seatScheduleInfoId.toString());
        redisTemplate.expire(redisSelectedKey, Duration.ofMinutes(5));

        String redisKey = RedisKeyProvider.seatStatusKey(scheduleId);
        redisTemplate.opsForHash().put(redisKey, seatScheduleInfoId.toString(), SeatStatus.SELECTED.name());
    }

    public Map<String, String> getSeatStatusMap(Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차가 존재하지 않습니다."));

        List<SeatScheduleInfo> seatScheduleInfos =
                seatScheduleInfoRepository.findAllBySchedule(schedule);

        List<String> fieldKeys = seatScheduleInfos.stream()
                .map(info -> info.getId().toString())
                .toList();

        String redisKey = RedisKeyProvider.seatStatusKey(scheduleId);
        List<Object> redisStatuses = redisTemplate.opsForHash().multiGet(redisKey, new ArrayList<>(fieldKeys));
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

    public void forceSeatScheduleInfoInRedis(Long scheduleId){
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차가 존재하지 않습니다."));

        List<SeatScheduleInfo> seatInfos = seatScheduleInfoRepository.findAllBySchedule(schedule);

        String redisHashKey = RedisKeyProvider.seatStatusKey(scheduleId);
        Map<String, String> seatStatusMap = new HashMap<>();

        for (SeatScheduleInfo seat : seatInfos) {
            seatStatusMap.put(seat.getId().toString(), seat.getStatus().name());
        }

        redisTemplate.opsForHash().putAll(redisHashKey, seatStatusMap);
    }

    @Transactional
    public void updateSeatSchedulerInfoStatus(Long scheduleId, Long seatScheduleInfoId, SeatStatus seatStatus){
        String redisKey = RedisKeyProvider.seatStatusKey(scheduleId);
        String fieldKey = seatScheduleInfoId.toString();

        String status = (String) redisTemplate.opsForHash().get(redisKey, fieldKey);

        SeatScheduleInfo seatScheduleInfo = seatScheduleInfoRepository.findById(seatScheduleInfoId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차별 좌석 정보가 존재하지 않습니다."));

        seatScheduleInfo.updateSeatScheduleInfoStatus(seatStatus);
    }
}
