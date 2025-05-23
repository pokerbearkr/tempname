package org.example.siljeun.domain.schedule.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepository {

  List<Schedule> findByConcertId(Long concertId);

  List<Schedule> findAllByTicketingStartTimeBetween(LocalDateTime ticketingStartTimeAfter,
      LocalDateTime ticketingStartTimeBefore);

  Optional<Schedule> findById(Long id);

  List<Schedule> findAllByStartTimeAfterAndTicketingStartTimeBefore(LocalDateTime now,
      LocalDateTime now1);

  List<Schedule> findByIdInAndStartTimeBefore(ArrayList<Long> longs, LocalDateTime now);
}
