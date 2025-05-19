package org.example.siljeun.domain.concert.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.siljeun.domain.concert.entity.ConcertCategory;

public record ConcertCreateRequest(@NotBlank
                                   String title,
                                   String description,
                                   @NotNull
                                   ConcertCategory category,
                                   @NotNull Long venuId,
                                   Integer cancleCharge) {

}
