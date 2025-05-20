package org.example.siljeun.domain.schedule.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ScheduleCreateRequest(
    @NotNull LocalDateTime startTime,
    @NotNull LocalDateTime ticketingStartTime,
    @NotNull Long concertId
) {

}
