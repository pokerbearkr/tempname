package org.example.siljeun.domain.schedule.repository;

import java.util.List;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  List<Schedule> findByConcertId(Long concertId);
}
