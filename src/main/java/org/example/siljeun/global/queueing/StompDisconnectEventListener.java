package org.example.siljeun.global.queueing;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.service.WaitingQueueService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class StompDisconnectEventListener {

  private final WaitingQueueService waitingQueueService;

  @EventListener
  public void handleDisconnect(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

    String username = (String) accessor.getSessionAttributes().get("username");
    Long scheduleId = (Long) accessor.getSessionAttributes().get("scheduleId");

    if (username != null && scheduleId != null) {
      waitingQueueService.deleteAtQueue(scheduleId, username);
    }
  }
}
