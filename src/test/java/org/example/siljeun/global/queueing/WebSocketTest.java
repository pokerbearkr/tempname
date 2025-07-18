package org.example.siljeun.global.queueing;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.example.siljeun.domain.concert.entity.Concert;
import org.example.siljeun.domain.concert.entity.ConcertCategory;
import org.example.siljeun.domain.concert.repository.ConcertRepository;
import org.example.siljeun.domain.reservation.dto.response.MyQueueInfoResponse;
import org.example.siljeun.domain.reservation.service.WaitingQueueService;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.venue.entity.Venue;
import org.example.siljeun.domain.venue.repository.VenueRepository;
import org.example.siljeun.global.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
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
  private WaitingQueueService waitingQueueService;

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

    URI uri = new URI(
        "ws://localhost:" + port + "/ws?scheduleId=" + savedSchedule.getId());

    WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
    webSocketHttpHeaders.add("Authorization", validToken);

    StompHeaders stompHeaders = new StompHeaders();

    StompSession session = stompClient.connectAsync(uri, webSocketHttpHeaders, stompHeaders,
        new StompSessionHandlerAdapter() {
        }
    ).get(5, TimeUnit.SECONDS);

    assertTrue(session.isConnected());
  }

  @Test
  @Transactional
  void 대기번호_응답_성공()
      throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
    // given
    // 소켓 연결
    WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    URI uri = new URI(
        "ws://localhost:" + port + "/ws?scheduleId=1");
    WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
    webSocketHttpHeaders.add("Authorization", validToken);

    StompHeaders stompHeaders = new StompHeaders();
    StompSession session = stompClient.connectAsync(uri, webSocketHttpHeaders, stompHeaders,
        new StompSessionHandlerAdapter() {
        }
    ).get(5, TimeUnit.SECONDS);

    // 메시지 수신 대기용 변수
    CompletableFuture<MyQueueInfoResponse> completableFuture = new CompletableFuture<>();

    String destination = "/topic/queue/1/user1002";
    session.subscribe(destination, new StompFrameHandler() {

      @Override
      public Type getPayloadType(StompHeaders headers) {
        return MyQueueInfoResponse.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        completableFuture.complete((MyQueueInfoResponse) payload);
      }
    });

    // key 설정
    String key = waitingQueueService.prefixKeyForWaitingQueue + 1L;

    // when
    waitingQueueService.sendWaitingNumber(key, "user1002", 1L);

    // then
    MyQueueInfoResponse response = completableFuture.get(5, TimeUnit.SECONDS); // 메시지 수신 대기
    assertThat(response.username()).isEqualTo("user1002");
    assertThat(response.scheduleId()).isEqualTo(1);
    assertThat(response.rank()).isGreaterThanOrEqualTo(1);
  }
}
