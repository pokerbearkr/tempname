package org.example.siljeun.domain.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.service.WaitingQueueService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WaitingQueueController {

  private final WaitingQueueService waitingQueueService;

  @MessageMapping("/addQueue")
  public void addQueue(Message<?> message) {
    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);

    String username = (String) accessor.getSessionAttributes().get("username");
    Long scheduleId = Long.valueOf((String) accessor.getSessionAttributes().get("scheduleId"));

    //Long scheduleId = request.scheduleId();
    //String username = request.username();
    waitingQueueService.addWaitingQueue(scheduleId, username);
    System.out.println("연결 성공");
  }
}
