package org.example.siljeun.domain.schedule.repository;

import java.util.List;
import org.example.siljeun.domain.schedule.entity.Schedule;

public interface ScheduleQueryRepository {
  
  List<Schedule> findByConcertIdWithConcert(Long concertId);

}
