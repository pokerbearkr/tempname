package org.example.siljeun.domain.seat.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SeatCreateRequest(
        @NotBlank String section,
        @NotBlank String row,
        @NotBlank String column,
        @NotBlank String defaultGrade,
        @Min(0) int defaultPrice
) {
}
