package org.example.siljeun.domain.concert.dto.response;

public record ConcertSimpleResponse(
    Long id,
    String title,
    String venueName,
    String category
) {

}
