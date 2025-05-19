package org.example.siljeun.domain.concert.dto.response;

import java.util.List;
import lombok.Getter;
import org.example.siljeun.domain.concert.entity.ConcertCategory;
import org.example.siljeun.domain.schedule.dto.response.ScheduleSimpleResponse;
import org.example.siljeun.domain.venue.dto.response.VenueSimpleResponse;

@Getter
public class ConcertDetailResponse {

  private Long id;
  private String title;
  private String description;
  private ConcertCategory category;
  private VenueSimpleResponse venue;
  private List<ScheduleSimpleResponse> schedules;

  public ConcertDetailResponse(Long id, String title, String description, ConcertCategory category,
      VenueSimpleResponse venue, List<ScheduleSimpleResponse> schedules) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.category = category;
    this.venue = venue;
    this.schedules = schedules;
  }
}
