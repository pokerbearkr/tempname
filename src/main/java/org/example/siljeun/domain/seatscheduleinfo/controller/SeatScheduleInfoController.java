package org.example.siljeun.domain.seatscheduleinfo.controller;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.seatscheduleinfo.service.SeatScheduleInfoService;
import org.example.siljeun.global.security.PrincipalDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/schedules/{scheduleId}")
public class SeatScheduleInfoController {

    private final SeatScheduleInfoService seatScheduleInfoService;

    @PostMapping("/seat-schedule-infos/{seatScheduleInfoId}")
    public ResponseEntity<String> selectSeat(
            @PathVariable Long scheduleId,
            @PathVariable Long seatScheduleInfoId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        seatScheduleInfoService.selectSeat(userDetails.getUserId(), scheduleId, seatScheduleInfoId);
        return ResponseEntity.ok("좌석이 선택되었습니다.");
    }

    @GetMapping("/seat-schedule-infos")
    public ResponseEntity<Map<String, String>> getSeatScheduleInfos(
            @PathVariable Long scheduleId
    ) {
        return ResponseEntity.ok(seatScheduleInfoService.getSeatStatusMap(scheduleId));
    }
}
