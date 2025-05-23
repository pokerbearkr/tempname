package org.example.siljeun.domain.seatscheduleinfo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.siljeun.domain.seat.enums.SeatStatus;

//for PUT API
public record SeatScheduleUpdateInfoRequest(
        @NotNull Long seatScheduleInfoId,
        @NotNull SeatStatus status,
        @NotBlank String grade,
        @Min(0) int price
) {
}
