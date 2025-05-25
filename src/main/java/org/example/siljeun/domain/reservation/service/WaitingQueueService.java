package org.example.siljeun.domain.reservation.service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.reservation.dto.response.MyQueueInfoResponse;
import org.example.siljeun.domain.reservation.exception.CustomException;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WaitingQueueService {

  private final StringRedisTemplate redisTemplate;
  private final SimpMessagingTemplate messagingTemplate;
  private final ScheduleRepository scheduleRepository;

  private static final long ttlMillis = 900000L; // ttl 15분
  private static final long acceptedRank = 1000L; // 좌석 선택 최대 수용 인원 1000명
  public static final String prefixKeyForWaitingQueue = "waiting:schedule:";
  public static final String prefixKeyForSelecingQueue = "selecting:schedule:";

  // redis 연결 확인
  @PostConstruct
  public void testRedisConnection() {
    String pong = redisTemplate.getConnectionFactory().getConnection().ping();
    log.info("Redis 연결 상태: {}", pong);
  }

  // 예매 대기 시작
  public void addWaitingQueue(Long scheduleId, String username) {
    ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SCHEDULE));

    if (LocalDateTime.now().isBefore(schedule.getTicketingStartTime())) {
      throw new CustomException(ErrorCode.NOT_TICKETING_TIME);
    }

    String key = prefixKeyForWaitingQueue + scheduleId;
    long createdAt = System.currentTimeMillis();

    if (zSet.score(key, username) == null) {
      zSet.add(key, username, createdAt);
    }

    sendWaitingNumber(key, username, scheduleId);
  }

  // 좌석 선택 중인 유저 큐에 insert (TTL 관리용 큐)
  public void addSelectingQueue(Long scheduleId, String username) {
    ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

    String key = prefixKeyForSelecingQueue + scheduleId;
    Long expiredAt = System.currentTimeMillis() + ttlMillis;

    if (zSet.score(prefixKeyForSelecingQueue + scheduleId, username) == null) {
      zSet.add(key, username, expiredAt);
    }
  }

  // 대기 끝 or 소켓 연결 해제되면 대기열에서 삭제
  public void deleteWaitingUser(Long scheduleId, String username) {
    String key = prefixKeyForWaitingQueue + scheduleId;
    redisTemplate.opsForZSet().remove(key, username);
  }

  // 좌석 선택 완료 or 소켓 연결 해제 or TTL 만료되면 큐에서 삭제
  public void deleteSelectingUser(Long scheduleId, String username) {
    String key = prefixKeyForSelecingQueue + scheduleId;
    redisTemplate.opsForZSet().remove(key, username);
    sendAllWaitingNumber(scheduleId);
  }

  // 정상적인 경로로 좌석 선택 api 호출했는지 검증
  public boolean hasPassedWaitingQueue(Long scheduleId, String username) {
    return
        redisTemplate.opsForZSet().score(prefixKeyForSelecingQueue + scheduleId, username) != null;
  }

  // 대기중인 특정 사용자에게 랭킹 및 대기번호 전송
  public void sendWaitingNumber(String key, String username, Long scheduleId) {
    ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

    Long rank = zSet.rank(key, username);

    if (rank == null) {
      throw new CustomException(ErrorCode.QUEUE_INSERT_FAIL);
    }

    rank = rank + 1;

    // 내 순위와 현재 좌석 선택 중인 사용자 수의 합이 수용 인원보다 적으면 대기 X
    Long selectingQueueSize = zSet.size(prefixKeyForSelecingQueue + scheduleId);
    selectingQueueSize = (selectingQueueSize == null) ? 0 : selectingQueueSize;

    if (rank + selectingQueueSize <= acceptedRank) {
      String destination = "/topic/queue/" + scheduleId + "/" + username;
      MyQueueInfoResponse response = new MyQueueInfoResponse(scheduleId, username, rank,
          true);

      addSelectingQueue(scheduleId, username);
      deleteWaitingUser(scheduleId, username);

      messagingTemplate.convertAndSend(destination, response);

      return;
    }

    String destination = "/topic/queue/" + scheduleId + "/" + username;
    MyQueueInfoResponse response = new MyQueueInfoResponse(scheduleId, username, rank,
        false);
    messagingTemplate.convertAndSend(destination, response);
  }

  // 대기중인 모든 사용자에게 랭킹 및 대기번호 전송
  // Todo : redis pubsub -> disconnectListener에서 publish
  public void sendAllWaitingNumber(Long scheduleId) {
    String key = prefixKeyForWaitingQueue + scheduleId;

    // for문이나 stream으로 scheduleId에 해당하는 value값 리스트 추출
    Set<String> usernames = redisTemplate.opsForZSet().range(key, 0, -1);

    if (usernames == null || usernames.isEmpty()) {
      return;
    }

    // 해당 유저들한테 메세지 전송
    for (String username : usernames) {
      sendWaitingNumber(key, username, scheduleId);
    }
  }
}
