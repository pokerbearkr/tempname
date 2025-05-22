package org.example.siljeun.domain.seat.repository;

import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.seat.entity.SeatScheduleInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatScheduleInfoRepository extends JpaRepository<SeatScheduleInfo, Long> {

    List<SeatScheduleInfo> findAllBySchedule(Schedule schedule);
}
