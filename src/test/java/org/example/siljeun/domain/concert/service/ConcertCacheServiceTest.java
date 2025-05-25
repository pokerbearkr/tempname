package org.example.siljeun.domain.concert.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.example.siljeun.domain.concert.dto.response.ConcertDetailResponse;
import org.example.siljeun.domain.concert.entity.Concert;
import org.example.siljeun.domain.concert.entity.ConcertCategory;
import org.example.siljeun.domain.concert.repository.ConcertRepository;
import org.example.siljeun.domain.venue.entity.Venue;
import org.example.siljeun.domain.venue.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ConcertCacheServiceTest {

  @Autowired
  private ConcertRepository concertRepository;

  @Autowired
  private ConcertCacheService concertCacheService;

  @Autowired
  private VenueRepository venueRepository;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private List<Long> testConcertIds;

  @BeforeEach
  void setUp() {
    Venue venue = venueRepository.save(new Venue("테스트 공연장", "서울", 500));

    testConcertIds = IntStream.rangeClosed(1, 10)
        .mapToObj(i -> {
          Concert concert = Concert.builder()
              .title("공연" + i)
              .description("설명" + i)
              .category(ConcertCategory.CONCERT)
              .venue(venue)
              .cancelCharge(0)
              .build();
          concert = concertRepository.save(concert);

          // 캐시용 DTO 미리 저장
          ConcertDetailResponse response = new ConcertDetailResponse(
              concert.getId(), concert.getTitle(), concert.getDescription(),
              concert.getCategory(), null, List.of()
          );
          concertCacheService.saveConcertDetailCache(concert.getId(), response);

          return concert.getId();
        })
        .toList();
  }

  @Test
  void compareDbVsRedisCachePerformance() {
    int iterations = 10000;

    // 워밍업
    for (Long id : testConcertIds) {
      concertRepository.findById(id);
      concertCacheService.getConcertDetailCache(id);
    }

    long dbStart = System.nanoTime();
    for (int i = 0; i < iterations; i++) {
      for (Long id : testConcertIds) {
        concertRepository.findById(id);
      }
    }
    long dbEnd = System.nanoTime();

    long redisStart = System.nanoTime();
    for (int i = 0; i < iterations; i++) {
      for (Long id : testConcertIds) {
        concertCacheService.getConcertDetailCache(id);
      }
    }
    long redisEnd = System.nanoTime();

    long dbDuration = TimeUnit.NANOSECONDS.toMillis(dbEnd - dbStart);
    long redisDuration = TimeUnit.NANOSECONDS.toMillis(redisEnd - redisStart);

    System.out.println("🔵 DB 직접 조회 총 소요 시간: " + dbDuration + " ms");
    System.out.println("🟢 Redis 캐시 기반 조회 총 소요 시간: " + redisDuration + " ms");

    // Redis 캐싱 성능이 DB보다 확실히 좋아야 한다
    assertTrue(redisDuration < dbDuration, "Redis 캐싱 조회가 DB 조회보다 빨라야 합니다.");
  }
}
