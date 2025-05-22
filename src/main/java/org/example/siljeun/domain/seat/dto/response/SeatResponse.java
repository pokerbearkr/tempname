package org.example.siljeun.domain.seat.dto.response;

public record SeatResponse(
        Long seatId,
        String section,
        String row,
        String column,
        String seatNumber,
        String defaultGrade,
        int defaultPrice
) {
}
