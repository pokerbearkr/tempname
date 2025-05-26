package org.example.siljeun.domain.concert.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.concert.dto.request.ConcertCreateRequest;
import org.example.siljeun.domain.concert.dto.request.ConcertUpdateRequest;
import org.example.siljeun.domain.concert.dto.response.ConcertDetailResponse;
import org.example.siljeun.domain.concert.dto.response.ConcertSimpleResponse;
import org.example.siljeun.domain.concert.entity.Concert;
import org.example.siljeun.domain.concert.repository.ConcertRepository;
import org.example.siljeun.domain.schedule.dto.response.ScheduleSimpleResponse;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.user.repository.UserRepository;
import org.example.siljeun.domain.venue.dto.response.VenueSimpleResponse;
import org.example.siljeun.domain.venue.entity.Venue;
import org.example.siljeun.domain.venue.repository.VenueRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertServiceImpl implements ConcertService {

  private final ConcertRepository concertRepository;
  private final VenueRepository venueRepository;
  private final UserRepository userRepository;
  private final ScheduleRepository scheduleRepository;
  private final ConcertCacheService concertCacheService;
  private final RedisTemplate redisTemplate;


  @Override
  @Transactional
  public Long createConcert(ConcertCreateRequest request, Long userId) {
    Venue venue = venueRepository.findByIdAndDeletedAtIsNull(request.venuId())
        .orElseThrow(() -> new EntityNotFoundException("존재하지 않거나 삭제된 공연장입니다."));

    Concert concert = Concert.builder()
        .title(request.title())
        .description(request.description())
        .category(request.category())
        .venue(venue)
        .cancelCharge(request.cancleCharge())
        .build();

    return concertRepository.save(concert).getId();
  }

  @Override
  @Transactional
  public void updateConcert(Long concertId, ConcertUpdateRequest request) {
    Concert concert = concertRepository.findById(concertId)
        .orElseThrow(() -> new EntityNotFoundException("해당 공연이 존재하지 않습니다."));

    Venue venue = venueRepository.findByIdAndDeletedAtIsNull(request.venueId())
        .orElseThrow(() -> new EntityNotFoundException("존재하지 않거나 삭제된 공연장입니다."));

    concert.update(
        request.title(),
        request.description(),
        request.category(),
        venue,
        request.cancelCharge()
    );
  }

  @Override
  @Transactional
  public void deleteConcert(Long concertId) {
    concertRepository.deleteById(concertId);
  }

  @Override
  public List<ConcertSimpleResponse> getConcertList() {
    return concertRepository.findAll().stream()
        .map(concert -> new ConcertSimpleResponse(
            concert.getId(),
            concert.getTitle(),
            concert.getVenue().getName(),
            concert.getCategory().name()
        ))
        .toList();
  }

  @Override
  public ConcertDetailResponse getConcertDetail(Long concertId) {

    ConcertDetailResponse cached = concertCacheService.getConcertDetailCache(concertId);
    if (cached != null) {
      concertCacheService.increaseViewCount(concertId);
      return cached;
    }

    concertCacheService.increaseViewCount(concertId);

    Concert concert = concertRepository.findById(concertId)
        .orElseThrow(() -> new EntityNotFoundException("해당 공연이 존재하지 않습니다."));

    Venue venue = concert.getVenue();
    if (venue.isDeleted()) {
      throw new IllegalStateException("해당 공연장은 삭제되어 공연 상세 정보를 조회할 수 없습니다.");
    }

    VenueSimpleResponse venueResponse = new VenueSimpleResponse(
        concert.getVenue().getId(),
        concert.getVenue().getName(),
        concert.getVenue().getLocation(),
        concert.getVenue().getSeatCapacity()
    );

    List<ScheduleSimpleResponse> schedules = scheduleRepository.findByConcertId(concertId).stream()
        .map(schedule -> new ScheduleSimpleResponse(
            schedule.getId(),
            schedule.getStartTime(),
            schedule.getTicketingStartTime()
        ))
        .toList();

    ConcertDetailResponse response = new ConcertDetailResponse(
        concert.getId(),
        concert.getTitle(),
        concert.getDescription(),
        concert.getCategory(),
        venueResponse,
        schedules
    );

    concertCacheService.saveConcertDetailCache(concertId, response);

    return response;
  }

  @Override
  public List<ConcertSimpleResponse> getDailyPopularConcerts() {
    List<Long> ids = concertCacheService.getTopConcertIds("ranking:daily", 7);
    return mapConcertsByIdOrder(ids);
  }

  @Override
  public List<ConcertSimpleResponse> getWeeklyPopularConcerts() {
    List<Long> ids = concertCacheService.getTopConcertIds("ranking:weekly", 7);
    return mapConcertsByIdOrder(ids);
  }

  private List<ConcertSimpleResponse> mapConcertsByIdOrder(List<Long> ids) {
    List<Concert> concerts = concertRepository.findByIdIn(ids);
    Map<Long, Concert> concertMap = concerts.stream()
        .collect(Collectors.toMap(Concert::getId, c -> c));
    return ids.stream()
        .map(concertMap::get)
        .filter(Objects::nonNull)
        .map(c -> new ConcertSimpleResponse(
            c.getId(), c.getTitle(), c.getVenue().getName(), c.getCategory().name()
        ))
        .toList();
  }


}
