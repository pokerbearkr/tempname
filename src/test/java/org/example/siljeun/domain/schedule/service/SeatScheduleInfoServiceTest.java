package org.example.siljeun.domain.schedule.service;

import org.example.siljeun.domain.concert.entity.Concert;
import org.example.siljeun.domain.concert.entity.ConcertCategory;
import org.example.siljeun.domain.concert.repository.ConcertRepository;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.schedule.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seat.entity.Seat;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.domain.venue.entity.Venue;
import org.example.siljeun.domain.venue.repository.VenueRepository;
import org.example.siljeun.domain.venue.repository.VenueSeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SeatScheduleInfoServiceTest {

    @Autowired
    private SeatScheduleInfoService seatScheduleInfoService;

    @Autowired
    private SeatScheduleInfoRepository seatScheduleInfoRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private VenueSeatRepository venueSeatRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    @Qualifier("redisLongTemplate")
    private RedisTemplate<String, Long> redisTemplate;

    private Seat seat;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        Venue venue = venueRepository.save(new Venue("샤롯데씨어터", "잠실 어딘가", 1));
        seat = venueSeatRepository.save(new Seat(venue, "A", "1", "1", "VIP", 180000));
        Concert concert = concertRepository.save(new Concert("위키드", "엘파바와 글린다", ConcertCategory.MUSICAL, venue, 1000));
        schedule = scheduleRepository.save(new Schedule(concert, LocalDateTime.of(2025, 6, 6, 14, 30), LocalDateTime.of(2025, 5, 6, 10, 0)));
    }

    @Test
    @DisplayName("동일 좌석 동시 요청: 1명만 성공하고 나머지는 선점 메시지")
    void sameSeatConcurrentAccessTest() throws InterruptedException {
        // given
        SeatScheduleInfo seatScheduleInfo = seatScheduleInfoRepository.save(new SeatScheduleInfo(seat, schedule, SeatStatus.AVAILABLE, seat.getDefaultGrade(), seat.getDefaultPrice()));
        Long seatScheduleInfoId = seatScheduleInfo.getId();
        int totalThreads = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(totalThreads);
        List<String> resultMessages = Collections.synchronizedList(new ArrayList<>());

        // when
        IntStream.range(0, totalThreads).forEach(i -> {
            executor.submit(() -> {
                try {
                    seatScheduleInfoService.selectSeat((long) i + 1, schedule.getId(), seatScheduleInfoId);
                    resultMessages.add("SUCCESS");
                } catch (ResponseStatusException e) {
                    resultMessages.add(e.getReason());
                } finally {
                    latch.countDown();
                }
            });
        });

        latch.await();

        // then
        long successCount = resultMessages.stream().filter("SUCCESS"::equals).count();
        long conflictCount = resultMessages.stream().filter("이미 선점된 좌석입니다."::equals).count();

        System.out.println("\n성공 요청 수: " + successCount);
        System.out.println("실패 요청 수: " + conflictCount);

        assertEquals(1, successCount);
        assertEquals(totalThreads - 1, conflictCount);
    }
}