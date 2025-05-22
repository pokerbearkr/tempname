package org.example.siljeun.domain.reservation.repository;

import org.example.siljeun.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
