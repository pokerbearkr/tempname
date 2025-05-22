package org.example.siljeun.domain.schedule.repository;

import java.util.List;
import java.util.Optional;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepository {

  List<Schedule> findByConcertId(Long concertId);

  Optional<Schedule> findById(Long id);
}
