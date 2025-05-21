package org.example.siljeun.domain.schedule.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.schedule.dto.request.ScheduleCreateRequest;
import org.example.siljeun.domain.schedule.dto.request.ScheduleUpdateRequest;
import org.example.siljeun.domain.schedule.dto.response.ScheduleSimpleResponse;
import org.example.siljeun.domain.schedule.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

  private final ScheduleService scheduleService;

  @PostMapping
  public ResponseEntity<ScheduleSimpleResponse> createSchedule(
      @RequestBody ScheduleCreateRequest request
  ) {
    ScheduleSimpleResponse response = scheduleService.createSchedule(request);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ScheduleSimpleResponse> updateSchedule(
      @PathVariable Long id,
      @RequestBody ScheduleUpdateRequest request
  ) {
    return ResponseEntity.ok(scheduleService.updateSchedule(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
    scheduleService.deleteSchedule(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<ScheduleSimpleResponse>> getSchedulesByConcertId(
      @RequestParam Long concertId
  ) {
    List<ScheduleSimpleResponse> response = scheduleService.getSchedulesByConcertId(concertId);
    return ResponseEntity.ok(response);
  }

}
