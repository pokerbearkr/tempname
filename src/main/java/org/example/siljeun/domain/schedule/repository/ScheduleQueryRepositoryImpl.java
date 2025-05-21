package org.example.siljeun.domain.schedule.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.concert.entity.QConcert;
import org.example.siljeun.domain.schedule.entity.QSchedule;
import org.example.siljeun.domain.schedule.entity.Schedule;


@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final QSchedule schedule = QSchedule.schedule;
  private final QConcert concert = QConcert.concert;

  @Override
  public List<Schedule> findByConcertIdWithConcert(Long concertId) {
    return queryFactory
        .selectFrom(schedule)
        .join(schedule.concert, concert).fetchJoin()
        .where(schedule.concert.id.eq(concertId))
        .fetch();
  }

}
