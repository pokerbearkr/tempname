package org.example.siljeun.domain.concert.scheduler;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.concert.repository.ConcertRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConcertViewSyncScheduler {
  
  private final RedisTemplate<String, String> redisTemplate;
  private final ConcertRepository concertRepository;

  public ConcertViewSyncScheduler(
      @Qualifier("redisStringTemplate") RedisTemplate<String, String> redisTemplate,
      ConcertRepository concertRepository
  ) {
    this.redisTemplate = redisTemplate;
    this.concertRepository = concertRepository;
  }

  @Scheduled(fixedRate = 600_000) // 10분 마다 실행
  public void syncViewCountsToDatabase() {
    Set<String> keys = redisTemplate.keys("concert:viewCount.*");

    if (keys == null || keys.isEmpty()) {
      return;
    }

    for (String key : keys) {
      try {
        Long concertId = Long.valueOf(key.replace("concert:viewCount.", ""));
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
          continue;
        }

        Long viewCount = Long.parseLong(value);

        concertRepository.findById(concertId).ifPresent(concert -> {
          concert.addViewCount(viewCount);
          concertRepository.save(concert);
        });

        redisTemplate.delete(key);
      } catch (Exception e) {
        log.warn("조회수 반영 중 오류 발생: key = {}", key, e);
      }
    }
  }
}
