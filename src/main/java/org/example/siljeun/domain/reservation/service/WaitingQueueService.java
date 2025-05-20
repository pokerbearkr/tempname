package org.example.siljeun.domain.reservation.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.response.MyQueueInfoResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitingQueueService {

  private final StringRedisTemplate redisTemplate;
  private final SimpMessagingTemplate messagingTemplate;

  // redis 연결 확인
  @PostConstruct
  public void testRedisConnection() {
    String pong = redisTemplate.getConnectionFactory().getConnection().ping();
    System.out.println("Redis 연결 상태: " + pong);
  }

  // 예매 대기 시작
  public void addQueue(Long scheduleId, Long userId) {
    long ttlMillis = 900000L; // 15분
    long acceptedRank = 1000L;
    String key = "queue:schedule:" + scheduleId;
    ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

    long expiredAt = System.currentTimeMillis() + ttlMillis;
    zSet.add(key, String.valueOf(userId),
        expiredAt);

    Long rank = zSet.rank(key, String.valueOf(userId));
    rank = (rank != null) ? rank + 1 : -1;

    String destination = "/topic/queue/" + scheduleId + "/" + userId;
    MyQueueInfoResponse response = new MyQueueInfoResponse(scheduleId, userId, rank, acceptedRank);
    messagingTemplate.convertAndSend(destination, response);
  }
}
