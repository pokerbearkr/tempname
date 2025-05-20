package org.example.siljeun.domain.venue.repository;

import org.example.siljeun.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueSeatRepository extends JpaRepository<Seat, Long> {

}
