package org.example.siljeun.domain.seatscheduleinfo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.seatscheduleinfo.dto.request.SeatScheduleUpdateStatusRequest;
import org.example.siljeun.domain.seatscheduleinfo.service.SeatScheduleInfoService;
import org.example.siljeun.global.dto.ResponseDto;
import org.example.siljeun.global.security.PrincipalDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SeatScheduleInfoController {

    private final SeatScheduleInfoService seatScheduleInfoService;

    @PostMapping("/schedules/{scheduleId}/seat-schedule-infos")
    public ResponseEntity<ResponseDto<Void>> forceSeatScheduleInfoInRedis(
            @PathVariable Long scheduleId
    )
    {
        seatScheduleInfoService.forceSeatScheduleInfoInRedis(scheduleId);
        return ResponseEntity.ok(ResponseDto.success("Redis 적재 완료 scheduleId : " + scheduleId, null));
    }

    @PostMapping("/schedules/{scheduleId}/seat-schedule-infos/{seatScheduleInfoId}")
    public ResponseEntity<ResponseDto<Void>> selectSeat(
            @PathVariable Long scheduleId,
            @PathVariable Long seatScheduleInfoId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        seatScheduleInfoService.selectSeat(userDetails.getUserId(), scheduleId, seatScheduleInfoId);
        return ResponseEntity.ok(ResponseDto.success( "좌석이 선택되었습니다. seatScheduleInfoId : " + seatScheduleInfoId.toString(), null));
    }

    @GetMapping("/schedules/{scheduleId}/seat-schedule-infos")
    public ResponseEntity<Map<String, String>> getSeatScheduleInfos(
            @PathVariable Long scheduleId
    ) {
        return ResponseEntity.ok(seatScheduleInfoService.getSeatStatusMap(scheduleId));
    }

//    @PatchMapping("/seat-schedule-infos")
//    public ResponseEntity<ResponseDto<Void>> updateSeatScheduleInfoStatus(
//            @RequestBody @Valid SeatScheduleUpdateStatusRequest seatScheduleRequest
//    ){
//        seatScheduleInfoService.updateSeatSchedulerInfoStatus(seatScheduleRequest.seatScheduleInfoId(), seatScheduleRequest.status());
//        return ResponseEntity.ok(ResponseDto.success("좌석의 상태가 변경되었습니다.", null));
//    }
}
