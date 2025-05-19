package org.example.siljeun.domain.schedule.dto.request;

import java.time.LocalDateTime;

public record ScheduleUpdateRequest(
    LocalDateTime startTime,
    LocalDateTime ticketingStartTime
) {

}
