package org.example.siljeun.domain.venue.controller;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.venue.dto.request.VenueCreateRequest;
import org.example.siljeun.domain.venue.dto.request.VenueUpdateRequest;
import org.example.siljeun.domain.venue.service.VenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/venues")
public class VenueController {

  private final VenueService venueService;

  @PostMapping
  public ResponseEntity<Long> createVenue(@RequestBody @Valid VenueCreateRequest request) {
    Long venueId = venueService.createVenue(request);
    return ResponseEntity.created(URI.create("/venues/" + venueId)).body(venueId);
  }

  @PutMapping("/{venueId}")
  public ResponseEntity<Void> updateVenue(@PathVariable Long venueId,
      @RequestBody @Valid VenueUpdateRequest request) {
    venueService.updateVenue(venueId, request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{venueId}")
  public ResponseEntity<Void> deleteVenue(@PathVariable Long venueId) {
    venueService.deleteVenue(venueId);
    return ResponseEntity.noContent().build();
  }
}
