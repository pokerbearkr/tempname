package org.example.siljeun.domain.seat.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.seat.dto.request.SeatCreateRequest;
import org.example.siljeun.domain.seat.entity.Seat;
import org.example.siljeun.domain.venue.entity.Venue;
import org.example.siljeun.domain.venue.repository.VenueRepository;
import org.example.siljeun.domain.seat.repository.SeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public void createSeats(Long venueId, List<SeatCreateRequest> seatCreateRequests){
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 공연장을 찾을 수 없습니다."));         //Throw 예외 설정 필요

        //공연장 ID, 구역, 열, 번호를 바탕으로 고유하도록 설정하였으나
        //좌석 정보가 중복되는 경우를 다루지 않아 추후 리팩토링이 필요함
        List<Seat> seats = seatCreateRequests.stream()
                .map(request -> Seat.from(venue, request))
                .toList();

        seatRepository.saveAll(seats);
    }
}
