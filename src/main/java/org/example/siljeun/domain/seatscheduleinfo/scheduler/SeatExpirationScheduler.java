package org.example.siljeun.domain.seatscheduleinfo.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.domain.seatscheduleinfo.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seatscheduleinfo.repository.SeatScheduleInfoRepository;
import org.example.siljeun.global.util.RedisKeyProvider;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SeatExpirationScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final SeatScheduleInfoRepository seatScheduleInfoRepository;

    @Scheduled(fixedDelay = 60_000)
    public void expireSeatsToAvailable() {
        long now = System.currentTimeMillis();
        expireByStatus(SeatStatus.SELECTED, now);
        expireByStatus(SeatStatus.HOLD, now);
    }

    private void expireByStatus(SeatStatus status, long nowMillis) {
        String zsetKey = RedisKeyProvider.trackExpiresKey(status.name());
        //상태-> 좌석Id들 1, 2, 3, 4,..... + 만료 시간
        //중에서 만료 시간이 지금 이전인 것들 조회
        Set<String> expiredIds = redisTemplate
                .opsForZSet()
                .rangeByScore(zsetKey, 0, nowMillis);
        if (expiredIds == null || expiredIds.isEmpty()) {

            return;
        }

        //만료 시간이 지난 Id들을 Long 타입으로 변경하고 실제 객체를 가져와서 상태를 변경 후 저장
        List<Long> ids = expiredIds.stream()
                .map(Long::valueOf)
                .toList();
        List<SeatScheduleInfo> infos = seatScheduleInfoRepository.findAllById(ids);
        infos.forEach(info -> info.updateSeatScheduleInfoStatus(SeatStatus.AVAILABLE));
        seatScheduleInfoRepository.saveAll(infos);

        final Map<String, Map<Object,Object>> hashBatch = new HashMap<>();
        for (SeatScheduleInfo info : infos) {
            String hashKey = RedisKeyProvider.seatStatusKey(info.getSchedule().getId());
            hashBatch
                    .computeIfAbsent(hashKey, k -> new HashMap<>())
                    .put(info.getId().toString(), SeatStatus.AVAILABLE);
        }

        RedisCallback<Object> pipelineWork = connection -> {
            // ZSET 제거
            connection.zRem(
                    zsetKey.getBytes(),
                    expiredIds.stream()
                            .map(String::getBytes)
                            .toArray(byte[][]::new)
            );

            // 해시 업데이트
            for (Map.Entry<String, Map<Object,Object>> e : hashBatch.entrySet()) {
                byte[] hashKey = redisTemplate.getStringSerializer().serialize(e.getKey());
                Map<byte[], byte[]> serialized = new HashMap<>();
                e.getValue().forEach((field, value) ->
                        serialized.put(
                                redisTemplate.getStringSerializer().serialize(field.toString()),
                                redisTemplate.getStringSerializer().serialize(value.toString())
                        )
                );
                connection.hMSet(hashKey, serialized);
            }

            return null;
        };

        redisTemplate.executePipelined(pipelineWork);
    }
}