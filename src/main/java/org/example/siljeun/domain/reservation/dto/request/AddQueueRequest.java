package org.example.siljeun.domain.reservation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddQueueRequest(
    @NotBlank
    Long scheduleId,
    @NotBlank
    String username
) {

}
