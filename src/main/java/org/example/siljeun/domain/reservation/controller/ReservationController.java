package org.example.siljeun.domain.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping("/schedules/{scheduleId}/reservations")
  public ResponseEntity<Response> create(@PathVariable Long scheduleId) {
    // Todo : user 정보 가져와서 service에 전달
    reservationService.create(scheduleId);
    return null;
  }
}
