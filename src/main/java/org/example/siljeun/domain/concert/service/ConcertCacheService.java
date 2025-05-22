package org.example.siljeun.domain.concert.service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertCacheService {

  @Qualifier("redisJsonTemplate")
  private final RedisTemplate<String, Object> redisTemplate;

  private static final String RANK_KEY = "concert:ranking";

  public void increaseDailyViewCount(Long concertId) {
    String key = "ranking:daily";
    redisTemplate.opsForZSet().incrementScore(key, concertId, 1);
    redisTemplate.expire(key, Duration.ofDays(1));
  }

  public void increaseWeeklyViewCount(Long concertId) {
    String key = "ranking:weekly";
    redisTemplate.opsForZSet().incrementScore(key, concertId, 1);
    redisTemplate.expire(key, Duration.ofDays(7));
  }

  public List<Long> getTopConcertIds(int limit) {
    Set<Object> ids = redisTemplate.opsForZSet()
        .reverseRange(RANK_KEY, 0, limit - 1);

    return ids.stream()
        .map(id -> Long.valueOf(id.toString()))
        .toList();
  }

  public List<Long> getTopConcertIds(String key, int limit) {
    Set<Object> ids = redisTemplate.opsForZSet()
        .reverseRange(key, 0, limit - 1);
    return ids.stream().map(id -> Long.valueOf(id.toString())).toList();
  }

}
