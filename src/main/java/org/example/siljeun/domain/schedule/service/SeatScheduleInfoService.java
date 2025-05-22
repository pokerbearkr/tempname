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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        // userId와 schedule Id가 key이고 seatSchduleInfoId로 구성된 Set이 value인 형태로 저장
        String redisSelectedKey = "user:scheduleSelected" + userId + ":" + scheduleId;
        redisTemplate.opsForSet().add(redisSelectedKey, seatScheduleInfoId.toString());
        //key에 해당하는 set 데이터를 TTL 5분으로 업데이트
        redisTemplate.expire(redisSelectedKey, Duration.ofMinutes(5));

        //seatScheduleInfoId의 seatStatus 상태 변경
        redisTemplate.opsForValue().set("seatStatus:"+seatScheduleInfoId.toString(), SeatStatus.SELECTED.name());

        //user가 선점한 좌석들 TTL 5분으로 재설정
        Set<String> seatScheduleInfoIds = redisTemplate.opsForSet().members(redisSelectedKey);
        if (seatScheduleInfoIds != null) {
            for (String seatId : seatScheduleInfoIds) {
                String redisStatusKey = "seatStatus:" + seatId;
                redisTemplate.expire(redisStatusKey, Duration.ofMinutes(5)); // TTL 재설정
            }
        }
    }

    public Map<String, String> getSeatStatusMap(Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차가 존재하지 않습니다."));

        List<SeatScheduleInfo> seatScheduleInfos =
                seatScheduleInfoRepository.findAllBySchedule(schedule);

        Map<String, String> result = new HashMap<>();

        for (SeatScheduleInfo info : seatScheduleInfos) {
            String redisKey = "seatStatus:" + info.getId();
            String redisStatus = redisTemplate.opsForValue().get(redisKey);

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
