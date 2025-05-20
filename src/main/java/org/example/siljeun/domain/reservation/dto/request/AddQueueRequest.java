package org.example.siljeun.domain.reservation.dto.request;

public record AddQueueRequest(
    Long scheduleId,
    String username
) {

}
