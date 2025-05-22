package org.example.siljeun.domain.schedule.controller;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.schedule.service.SeatScheduleInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class SeatScheduleInfoController {

    private final SeatScheduleInfoService seatScheduleInfoService;

    @PostMapping("/seat-schedule-info/{seatScheduleInfoId}")
    public ResponseEntity<String> selectSeat(
        @PathVariable Long seatScheduleInfoId
    ){
        seatScheduleInfoService.selectSeat(1L, seatScheduleInfoId);

        return ResponseEntity.ok("좌석이 선택되었습니다.");
    }
}
