package org.example.siljeun.domain.reservation.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReservationCreateRequest(
        @NotNull Long scheduleId
)
{
}
