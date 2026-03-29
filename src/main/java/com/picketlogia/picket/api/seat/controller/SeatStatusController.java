package com.picketlogia.picket.api.seat.controller;

import com.picketlogia.picket.api.seat.model.dto.RockedSeats;
import com.picketlogia.picket.api.seat.service.SeatStatusService;
import com.picketlogia.picket.common.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seat-status")
public class SeatStatusController {

    private final SeatStatusService seatStatusService;
    private final SimpMessagingTemplate messagingTemplate;

    // 특정 회차의 현재 좌석 상태 전체 조회 (추가)
    @Operation(
            summary = "실시간 좌석 조회",
            description = "특정 회차의 현재 좌석 상태를 조회합니다."
    )
    @GetMapping("/{roundTimeIdx}")
    public ResponseEntity<BaseResponse<Map<Object, Object>>> getSeatStatusV2(@PathVariable Long roundTimeIdx) {

        Map<Object, Object> allSeatStatusV3 = seatStatusService.getAllSeatStatusV2(roundTimeIdx);
        return ResponseEntity.ok(BaseResponse.success(allSeatStatusV3));

    }

    // 특정 회차의 현재 좌석 상태 전체 조회 (추가)
    @Operation(
            summary = "실시간 좌석 잠금 해제",
            description = "특정 사용자가 실시간 좌석 웹소켓에 연결된 상태에서 선택한 좌석을 해제합니다."
    )
    @DeleteMapping("/{roundTimeIdx}")
    public void deleteRockedSeat(@PathVariable Long roundTimeIdx, @RequestBody RockedSeats rockedSeats) {

        seatStatusService.deleteRockedSeats(roundTimeIdx, rockedSeats);
        messagingTemplate.convertAndSend("/topic/seats/map/" + roundTimeIdx, rockedSeats.getSeatIdxes());
    }
}
