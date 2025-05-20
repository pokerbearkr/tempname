package org.example.siljeun.domain.schedule.service;

import org.example.siljeun.domain.schedule.dto.request.ScheduleCreateRequest;
import org.example.siljeun.domain.schedule.dto.request.ScheduleUpdateRequest;
import org.example.siljeun.domain.schedule.dto.response.ScheduleSimpleResponse;

public interface ScheduleService {

  ScheduleSimpleResponse createSchedule(ScheduleCreateRequest request);

  ScheduleSimpleResponse updateSchedule(Long id, ScheduleUpdateRequest request);

  void deleteSchedule(Long id);

}
