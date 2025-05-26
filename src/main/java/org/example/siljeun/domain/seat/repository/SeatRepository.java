package org.example.siljeun.domain.seat.repository;

import org.example.siljeun.domain.seat.entity.Seat;
import org.example.siljeun.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByVenue(Venue venue);
}
