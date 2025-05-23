package org.example.siljeun.domain.seatscheduleinfo.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.siljeun.domain.seat.enums.SeatStatus;

public record SeatScheduleInfoCreateRequest(
        @NotBlank Long seatId,
        @NotNull SeatStatus status,
        @Nullable String grade,
        @Nullable Integer price
) {
}
