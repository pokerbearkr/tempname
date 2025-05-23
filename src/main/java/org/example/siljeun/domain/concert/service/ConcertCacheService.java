package org.example.siljeun.domain.concert.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.concert.dto.response.ConcertDetailResponse;
import org.example.siljeun.domain.concert.entity.Concert;
import org.example.siljeun.domain.concert.repository.ConcertRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertCacheService {

  @Qualifier("redisJsonTemplate")
  private final RedisTemplate<String, Object> redisTemplate;

  private final ConcertRepository concertRepository;

  private static final String RANK_KEY = "concert:ranking";

  public void increaseViewCount(Long concertId) {
    String viewCountKey = "concert:viewCount:" + concertId;
    if (Boolean.FALSE.equals(redisTemplate.hasKey(viewCountKey))) {
      // 캐시에 없으면 DB에서 조회 후 Redis에 캐싱
      Long viewCount = concertRepository.findById(concertId)
          .map(Concert::getViewCount)
          .orElse(0L);
      redisTemplate.opsForValue().set(viewCountKey, viewCount);
    }

    redisTemplate.opsForValue().increment(viewCountKey);

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

  private static final String CONCERT_KEY_CACHE_PREFIX = "concert:info";
  private static final Duration TTL = Duration.ofMinutes(30);
  private final ObjectMapper objectMapper;

  public ConcertDetailResponse getConcertDetailCache(Long concertId) {
    String json = (String) redisTemplate.opsForValue().get(CONCERT_KEY_CACHE_PREFIX + concertId);
    if (json == null) {
      return null;
    }
    try {
      return objectMapper.readValue(json, ConcertDetailResponse.class);
    } catch (Exception e) {
      return null;
    }
  }

  public void saveConcertDetailCache(Long concertId, ConcertDetailResponse response) {
    try {
      String json = objectMapper.writeValueAsString(response);
      redisTemplate.opsForValue().set(CONCERT_KEY_CACHE_PREFIX + concertId, json, TTL);
    } catch (Exception e) {
      // 로그만 남기고 캐싱 실패는 무시
    }
  }

}
