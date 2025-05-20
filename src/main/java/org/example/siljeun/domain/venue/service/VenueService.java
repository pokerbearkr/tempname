package org.example.siljeun.domain.venue.service;

import org.example.siljeun.domain.venue.dto.request.VenueCreateRequest;
import org.example.siljeun.domain.venue.dto.request.VenueUpdateRequest;

public interface VenueService {

  Long createVenue(VenueCreateRequest request);

  void updateVenue(Long venueId, VenueUpdateRequest request);

  void deleteVenue(Long venueId);

}
