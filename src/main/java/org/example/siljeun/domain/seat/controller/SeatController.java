package org.example.siljeun.domain.seat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.seat.dto.request.SeatCreateRequest;
import org.example.siljeun.domain.seat.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/venues")
public class SeatController {

    private final SeatService seatService;

    //좌석 정보를 CSV 파일 또는 GUI로 다수의 정보를 한번에 등록한다.
    @PostMapping("/{venueId}/seats")
    public ResponseEntity<Void> createVenueSeats(
            @PathVariable Long venueId,
            @RequestBody @Valid List<SeatCreateRequest> seatCreateRequests
    ){
        seatService.createSeats(venueId, seatCreateRequests);
        return ResponseEntity.ok().build();
    }
}
