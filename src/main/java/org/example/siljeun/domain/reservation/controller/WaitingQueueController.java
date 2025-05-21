package org.example.siljeun.domain.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.dto.request.AddQueueRequest;
import org.example.siljeun.domain.reservation.service.WaitingQueueService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WaitingQueueController {

  private final WaitingQueueService waitingQueueService;

  @MessageMapping("/addQueue")
  public void addQueue(@Valid AddQueueRequest request) {
    Long scheduleId = request.scheduleId();
    String username = request.username();
    waitingQueueService.addQueue(scheduleId, username);
  }
}
