package org.example.siljeun.domain.schedule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
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

        if (!seatScheduleInfo.isAvailable()) {
            //log.info("이미 선점된 좌석입니다.");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 선점된 좌석입니다.");
        }

        seatScheduleInfo.updateSeatScheduleInfoStatus(SeatStatus.SELECTED);
        seatScheduleInfoRepository.save(seatScheduleInfo);

        //userId와 schedule Id가 key이고 seatSchduleInfoId로 구성된 Set이 value인 형태로 저장
        String redisSelectedKey = "user:scheduleSelected" + userId + ":" + scheduleId;
        redisTemplate.opsForSet().add(redisSelectedKey, seatScheduleInfoId.toString());
        //key에 해당하는 set 데이터를 TTL 5분으로 업데이트
        redisTemplate.expire(redisSelectedKey, Duration.ofMinutes(5));

        //seatScheduleInfoId의 seatStatus 상태 변경
        String redisHashKey = "schedule:seatStatus:" + scheduleId;
        redisTemplate.opsForHash().put(redisHashKey + scheduleId, seatScheduleInfoId.toString(), SeatStatus.SELECTED.name());
    }

    public Map<String, String> getSeatStatusMap(Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차가 존재하지 않습니다."));

        List<SeatScheduleInfo> seatScheduleInfos =
                seatScheduleInfoRepository.findAllBySchedule(schedule);

        List<String> fieldKeys = seatScheduleInfos.stream()
                .map(info -> info.getId().toString())
                .toList();

        String redisHashKey = "seatStatus:" + scheduleId;
        List<Object> redisStatuses = redisTemplate.opsForHash().multiGet(redisHashKey, new ArrayList<>(fieldKeys));

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
}
