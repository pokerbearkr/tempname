package org.example.siljeun;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.example.siljeun.domain.concert.entity.Concert;
import org.example.siljeun.domain.concert.entity.ConcertCategory;
import org.example.siljeun.domain.concert.repository.ConcertRepository;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.venue.entity.Venue;
import org.example.siljeun.domain.venue.repository.VenueRepository;
import org.example.siljeun.global.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebSocketTest {

  @LocalServerPort
  private int port;

  @Autowired
  private ScheduleRepository scheduleRepository;

  @Autowired
  private VenueRepository venueRepository;

  @Autowired
  private ConcertRepository concertRepository;

  @Autowired
  private JwtUtil jwtUtil;

  private String validToken;

  private Schedule savedSchedule;

  @BeforeEach
  void setup() {
    Concert savedConcert;
    Venue savedVenue;

    savedVenue = venueRepository.save(new Venue("name", "location", 1000));
    savedConcert = concertRepository.save(
        new Concert("title", "description", ConcertCategory.CONCERT, savedVenue, 1));
    savedSchedule = scheduleRepository.save(
        new Schedule(savedConcert, LocalDateTime.now(), LocalDateTime.now()));
    validToken = jwtUtil.createToken("testUser");
  }

  @Test
  void socket_connection_test() throws Exception {

    WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    URI uri = new URI("ws://localhost:" + port + "/ws?scheduleId=" + savedSchedule.getId());

    WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
    StompHeaders stompHeaders = new StompHeaders();
    webSocketHttpHeaders.add("Authorization", JwtUtil.BEARER_PREFIX + validToken);

    StompSession session = stompClient.connectAsync(uri, webSocketHttpHeaders, stompHeaders,
        new StompSessionHandlerAdapter() {
        }
    ).get(5, TimeUnit.SECONDS);

    assertTrue(session.isConnected());
  }
}
