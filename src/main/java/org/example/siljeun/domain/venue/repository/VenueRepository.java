package org.example.siljeun.domain.venue.repository;

import java.util.List;
import java.util.Optional;
import org.example.siljeun.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {

  List<Venue> findAllByDeletedAtIsNull();

  Optional<Venue> findByIdAndDeletedAtIsNull(Long id);
}
