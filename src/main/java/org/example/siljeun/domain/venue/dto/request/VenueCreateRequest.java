package org.example.siljeun.domain.venue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VenueCreateRequest(
    @NotBlank String name,
    @NotBlank String location,
    @NotNull Integer seatCapacity
) {

}
