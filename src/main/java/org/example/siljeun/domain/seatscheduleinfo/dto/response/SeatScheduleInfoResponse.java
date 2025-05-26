package org.example.siljeun.domain.seatscheduleinfo.dto.response;

public record SeatScheduleInfoResponse(
        Long seatScheduleInfoId,
        String seatNumber,
        String status,
        String grade,
        int price
) {
}