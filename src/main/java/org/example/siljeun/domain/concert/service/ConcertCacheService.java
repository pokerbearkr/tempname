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

  public void increaseViewCount(Long concertId) {
    redisTemplate.opsForZSet().incrementScore(RANK_KEY, concertId, 1);

    redisTemplate.opsForZSet().incrementScore("ranking:daily", concertId, 1);
    ensureTtl("ranking:daily", Duration.ofDays(1));

    redisTemplate.opsForZSet().incrementScore("ranking:weekly", concertId, 1);
    ensureTtl("ranking:weekly", Duration.ofDays(7));
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

  private void ensureTtl(String key, Duration ttl) {
    Long expire = redisTemplate.getExpire(key);
    if (expire == null || expire < 0) {
      redisTemplate.expire(key, ttl);
    }
  }

}
