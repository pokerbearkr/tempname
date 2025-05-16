package org.example.siljeun.domain.venue.repository;

import org.example.siljeun.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
  
}
