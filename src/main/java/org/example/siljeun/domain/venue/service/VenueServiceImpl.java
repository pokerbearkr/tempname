package org.example.siljeun.domain.venue.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.venue.dto.request.VenueCreateRequest;
import org.example.siljeun.domain.venue.dto.request.VenueUpdateRequest;
import org.example.siljeun.domain.venue.entity.Venue;
import org.example.siljeun.domain.venue.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueServiceImpl implements VenueService {

  private final VenueRepository venueRepository;

  @Override
  @Transactional
  public Long createVenue(VenueCreateRequest request) {
    Venue venue = new Venue(
        request.name(),
        request.location(),
        request.seatCapacity()
    );
    return venueRepository.save(venue).getId();
  }

  @Override
  @Transactional
  public void updateVenue(Long venueId, VenueUpdateRequest request) {
    Venue venue = venueRepository.findById(venueId)
        .orElseThrow(() -> new EntityNotFoundException("공연장을 찾을 수 없습니다."));

    venue.update(request.name(), request.location(), request.seatCapacity());
  }

  @Override
  public void deleteVenue(Long venueId) {
    venueRepository.deleteById(venueId);
  }

}
