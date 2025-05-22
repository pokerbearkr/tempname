package org.example.siljeun.domain.concert.repository;

import java.util.List;
import org.example.siljeun.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

  List<Concert> findByIdIn(List<Long> ids);

}
