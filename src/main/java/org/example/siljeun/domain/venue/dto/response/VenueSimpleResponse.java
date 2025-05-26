package org.example.siljeun.domain.venue.dto.response;

public record VenueSimpleResponse(
    Long id,
    String name,
    String location,
    Integer seatCapacity
) {

}
