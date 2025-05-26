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
  void _1001명_대기시_1001번째_유저가_대기0순위() {
    // given
    ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

    for (int i = 1; i <= 1001; i++) {
      waitingQueueService.addWaitingQueue(1L, "user" + i);
    }

    // when
    Long rank = zSet.rank(waitingQueueService.prefixKeyForWaitingQueue + 1L, "user1001");

    // then
    assertThat(rank).isEqualTo(0);
  }

  @Test
  @Transactional
  void selectingQueue에서_1명나가면_1002번이_대기0순위() {
    // given
    ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();
    waitingQueueService.addWaitingQueue(1L, "user1002");

    // when
    waitingQueueService.deleteSelectingUser(1L, "user1000");

    Long rank1001AtWaiting = zSet.rank(waitingQueueService.prefixKeyForWaitingQueue + 1L,
        "user1001");
    Long rank1001AtSelecting = zSet.rank(waitingQueueService.prefixKeyForSelecingQueue + 1L,
        "user1001");
    Long rank1002 = zSet.rank(waitingQueueService.prefixKeyForWaitingQueue + 1L, "user1002");

    // then
    assertThat(rank1001AtWaiting).isEqualTo(null);
    assertThat(rank1001AtSelecting).isEqualTo(999);
    assertThat(rank1002).isEqualTo(0);
  }
}