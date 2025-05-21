package org.example.siljeun.domain.reservation.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.reservation.dto.response.MyQueueInfoResponse;
import org.example.siljeun.domain.reservation.exception.CustomException;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingQueueService {

  private final StringRedisTemplate redisTemplate;
  private final SimpMessagingTemplate messagingTemplate;

  private static final long ttlMillis = 900000L; // ttl 15분
  private static final long acceptedRank = 1000L; // 좌석 선택 최대 수용 인원 1000명
  private static final String prefixKey = "queue:schedule:";

  // redis 연결 확인
  @PostConstruct
  public void testRedisConnection() {
    String pong = redisTemplate.getConnectionFactory().getConnection().ping();
    log.info("Redis 연결 상태: {}", pong);
  }

  // 예매 대기 시작
  public void addQueue(Long scheduleId, String username) {

    String key = prefixKey + scheduleId;
    long expiredAt = System.currentTimeMillis() + ttlMillis;
    ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

    if (zSet.score(key, username) == null) {
      zSet.add(key, username, expiredAt);
    }

    Long rank = zSet.rank(key, username);
    if (rank == null) {
      throw new CustomException(ErrorCode.QUEUE_INSERT_FAIL);
    }
    rank = rank + 1;

    String destination = "/topic/queue/" + scheduleId + "/" + username;
    MyQueueInfoResponse response = new MyQueueInfoResponse(scheduleId, username, rank,
        acceptedRank);
    messagingTemplate.convertAndSend(destination, response);
  }

  // 기존 유저가 좌석 선택 완료 or 소켓 연결 종료하면 대기열에서 삭제
  public void deleteAtQueue(Long scheduleId, String username) {
    redisTemplate.opsForZSet().remove(prefixKey + scheduleId, username);
    log.info("Disconnected and removed Schedule: {}, User: {}", scheduleId, username);

    // TTL 만료된 데이터 삭제
    redisTemplate.opsForZSet()
        .removeRangeByScore(prefixKey + scheduleId, 0, System.currentTimeMillis());

    // rank() 재실행해서 변경된 대기번호 클라이언트에 전송
    Long rank = redisTemplate.opsForZSet().rank(prefixKey + scheduleId, username);
    rank = (rank != null) ? rank + 1 : -1;

    String destination = "/topic/queue/" + scheduleId + "/" + username;
    MyQueueInfoResponse response = new MyQueueInfoResponse(scheduleId, username, rank,
        acceptedRank);
    messagingTemplate.convertAndSend(destination, response);
  }

  // sorted set에 해당 scheduleId, userId를 가지는 데이터가 존재하는지 확인
  public boolean checkQueue(Long scheduleId, String username) {
    boolean exists = redisTemplate.opsForZSet().score(prefixKey + scheduleId, username) != null;
    return exists;
  }
}
