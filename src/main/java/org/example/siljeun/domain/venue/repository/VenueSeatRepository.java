package org.example.siljeun.domain.venue.repository;

import org.example.siljeun.domain.seat.entity.Seat;
import org.example.siljeun.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueSeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByVenue(Venue venue);
}
