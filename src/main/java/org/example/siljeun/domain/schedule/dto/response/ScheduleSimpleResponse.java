package org.example.siljeun.domain.schedule.dto.response;

import java.time.LocalDateTime;

public record ScheduleSimpleResponse(
    Long id,
    LocalDateTime startTime,
    LocalDateTime ticketingStartTime
) {

}
