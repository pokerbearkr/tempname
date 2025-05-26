package org.example.siljeun.domain.reservation.dto.response;

public record MyQueueInfoResponse(
    Long scheduleId,
    String username,
    Long rank,
    boolean isPassable
) {

}
