package org.example.siljeun.domain.seat.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.siljeun.domain.seat.enums.SeatStatus;

//for PATCH API
public record SeatScheduleUpdateStatusRequest(
        @NotNull Long seatScheduleInfoId,
        @NotNull SeatStatus status
) {
}
