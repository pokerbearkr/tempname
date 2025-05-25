package org.example.siljeun.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class WaitingQueueServiceTest {

  private final WaitingQueueService waitingQueueService;
  private final StringRedisTemplate redisTemplate;

  @Autowired
  WaitingQueueServiceTest(WaitingQueueService waitingQueueService,
      StringRedisTemplate redisTemplate) {
    this.waitingQueueService = waitingQueueService;
    this.redisTemplate = redisTemplate;
  }

  @Test
  @Transactional
  void addWaitingQueue() {
    // given
    ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

    for (int i = 1; i <= 1001; i++) {
      waitingQueueService.addWaitingQueue(1L, "user" + i);
    }

    // when
    Long score = zSet.rank(waitingQueueService.prefixKeyForWaitingQueue + 1L, "user1001");

    // then
    assertThat(score).isEqualTo(0);
  }

  @Test
  void deleteWaitingUser() {
  }
}