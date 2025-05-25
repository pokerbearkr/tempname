package org.example.siljeun.domain.schedule.service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.concert.entity.Concert;
import org.example.siljeun.domain.concert.repository.ConcertRepository;
import org.example.siljeun.domain.schedule.dto.request.ScheduleCreateRequest;
import org.example.siljeun.domain.schedule.dto.request.ScheduleUpdateRequest;
import org.example.siljeun.domain.schedule.dto.response.ScheduleSimpleResponse;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.domain.seatscheduleinfo.repository.SeatScheduleInfoRepository;
import org.example.siljeun.domain.seat.entity.Seat;
import org.example.siljeun.domain.seatscheduleinfo.entity.SeatScheduleInfo;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.domain.seat.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final SeatScheduleInfoRepository seatScheduleInfoRepository;

    @Override
    @Transactional
    public ScheduleSimpleResponse createSchedule(ScheduleCreateRequest request) {
        Concert concert = concertRepository.findById(request.concertId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 공연입니다."));

        Schedule schedule = new Schedule(
                concert,
                request.startTime(),
                request.ticketingStartTime()
        );

        Schedule saved = scheduleRepository.save(schedule);

        //회차 생성 시, 회차별 좌석 정보도 함께 생성
        List<Seat> seats = seatRepository.findByVenue(concert.getVenue());

        List<SeatScheduleInfo> seatInfos = seats.stream()
                .map(seat -> SeatScheduleInfo.from(
                        seat,
                        schedule,
                        SeatStatus.AVAILABLE,
                        seat.getDefaultGrade(),
                        seat.getDefaultPrice())
                ).toList();

        seatScheduleInfoRepository.saveAll(seatInfos);

        return new ScheduleSimpleResponse(saved.getId(), saved.getStartTime(),
                saved.getTicketingStartTime());
    }

    @Override
    @Transactional
    public ScheduleSimpleResponse updateSchedule(Long id, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차가 존재하지 않습니다."));

        schedule.update(request.startTime(), request.ticketingStartTime());

        return new ScheduleSimpleResponse(schedule.getId(), schedule.getStartTime(),
                schedule.getTicketingStartTime());
    }

    @Override
    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차가 존재하지 않습니다."));

        seatScheduleInfoRepository.deleteBySchedule(schedule);
        scheduleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleSimpleResponse> getSchedulesByConcertId(Long concertId) {
        List<Schedule> schedules = scheduleRepository.findByConcertIdWithConcert(concertId);
        return schedules.stream()
                .map(
                        s -> new ScheduleSimpleResponse(s.getId(), s.getStartTime(), s.getTicketingStartTime()))
                .toList();
    }
}
