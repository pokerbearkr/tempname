package org.example.siljeun.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.schedule.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.global.lock.DistributedLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SeatScheduleInfoService {
    private final SeatScheduleInfoRepository seatScheduleInfoRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    @DistributedLock(key = "'seat:' + #seatScheduleInfoId")
    public void selectSeat(Long userId, Long seatScheduleInfoId) {
        SeatScheduleInfo seatScheduleInfo = seatScheduleInfoRepository.findById(seatScheduleInfoId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 회차의 좌석 정보를 찾을 수 없습니다."));

        if (!seatScheduleInfo.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 선점된 좌석입니다.");
        }

        seatScheduleInfo.updateSeatScheduleInfoStatus(SeatStatus.HOLD);
        seatScheduleInfoRepository.save(seatScheduleInfo);

        String redisKey = "seat:" + seatScheduleInfoId;
        redisTemplate.opsForValue().set(redisKey, userId, Duration.ofMinutes(5));
    }
}
