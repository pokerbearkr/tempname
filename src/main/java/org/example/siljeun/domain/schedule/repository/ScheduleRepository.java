package org.example.siljeun.domain.schedule.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepository {

  List<Schedule> findByConcertId(Long concertId);

  List<Schedule> findAllByTicketingStartTimeBetween(LocalDateTime ticketingStartTimeAfter, LocalDateTime ticketingStartTimeBefore);
}
