package org.example.siljeun.domain.schedule.repository;

import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatScheduleInfoRepository extends JpaRepository<SeatScheduleInfo, Long> {

}
