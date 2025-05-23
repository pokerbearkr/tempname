package org.example.siljeun.domain.reservation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddQueueRequest(
    @NotNull
    Long scheduleId,
    @NotBlank
    String username
) {

}
