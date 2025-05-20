package org.example.siljeun.domain.reservation.dto.response;

public record MyQueueInfoResponse(
    Long scheduleId,
    Long userId,
    Long rank,
    Long acceptedRank
) {

}
