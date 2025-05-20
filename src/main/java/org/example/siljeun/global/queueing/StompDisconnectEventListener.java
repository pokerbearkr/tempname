package org.example.siljeun.global.queueing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompDisconnectEventListener {

  private final StringRedisTemplate redisTemplate;

  @EventListener
  public void handleDisconnect(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

    String username = (String) accessor.getSessionAttributes().get("username");
    Long scheduleId = (Long) accessor.getSessionAttributes().get("scheduleId");

    if (username != null && scheduleId != null) {
      redisTemplate.opsForZSet().remove("queue:schedule:" + scheduleId, username);
      log.info("Disconnected and removed Schedule: " + scheduleId + " && User: " + username);
    }
  }
}
