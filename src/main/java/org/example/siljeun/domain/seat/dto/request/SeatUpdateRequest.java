package org.example.siljeun.domain.seat.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//for PUT API
public record SeatUpdateRequest(
        @NotNull Long seatId,
        @NotBlank String section,
        @NotBlank String row,
        @NotBlank String column,
        @NotBlank String defaultGrade,
        @Min(0) int defaultPrice
) {
}
