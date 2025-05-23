package org.example.siljeun.domain.seatscheduleinfo.controller;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.seatscheduleinfo.service.SeatScheduleInfoService;
import org.example.siljeun.global.dto.ResponseDto;
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

    @PostMapping("/seat-schedule-infos")
    public ResponseEntity<ResponseDto<Void>> forceSeatScheduleInfoInRedis(
            @PathVariable Long scheduleId
    )
    {
        seatScheduleInfoService.forceSeatScheduleInfoInRedis(scheduleId);
        return ResponseEntity.ok(ResponseDto.success("Redis 적재 완료 scheduleId : " + scheduleId, null));
    }

    @PostMapping("/seat-schedule-infos/{seatScheduleInfoId}")
    public ResponseEntity<ResponseDto<Void>> selectSeat(
            @PathVariable Long scheduleId,
            @PathVariable Long seatScheduleInfoId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        seatScheduleInfoService.selectSeat(userDetails.getUserId(), scheduleId, seatScheduleInfoId);
        return ResponseEntity.ok(ResponseDto.success( "좌석이 선택되었습니다. seatScheduleInfoId : " + seatScheduleInfoId.toString(), null));
    }

    @GetMapping("/seat-schedule-infos")
    public ResponseEntity<Map<String, String>> getSeatScheduleInfos(
            @PathVariable Long scheduleId
    ) {
        return ResponseEntity.ok(seatScheduleInfoService.getSeatStatusMap(scheduleId));
    }
}
