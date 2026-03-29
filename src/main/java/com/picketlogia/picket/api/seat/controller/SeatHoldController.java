package com.picketlogia.picket.api.seat.controller;

import com.picketlogia.picket.api.seat.dto.LockedSeats;
import com.picketlogia.picket.api.seat.service.SeatHoldService;
import com.picketlogia.picket.common.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rounds/{roundTimeId}/seats")
public class SeatHoldController {

    private final SeatHoldService seatHoldService;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(
            summary = "실시간으로 선택된 특정 회차의 좌석 목록을 조회",
            description = "사용자들이 결제전 선택한 좌석을 캐싱한 Redis에 접근하여 해당 회차의 선택된 좌석 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<Map<Object, Object>>> getHeldSeats(@PathVariable Long roundTimeId) {

        Map<Object, Object> heldSeats = seatHoldService.getHeldSeats(roundTimeId);
        return ResponseEntity.ok(BaseResponse.success(heldSeats));

    }

    @Operation(
            summary = "임시로 선택한 특정 회차의 모든 좌석을 해제",
            description = """ 
                    특정 회차에서 임시로 선택된 좌석들을 해제합니다.
                    대표적으로 사용자가 좌석을 임시로 선택한 상황에서 이전 버튼을 클릭하는 경우에 사용됩니다.
                    """
    )
    @DeleteMapping
    public void releaseSeats(@PathVariable Long roundTimeId, @RequestBody LockedSeats lockedSeats) {

        seatHoldService.releaseHeldSeats(roundTimeId, lockedSeats);
        messagingTemplate.convertAndSend("/topic/seats/map/" + roundTimeId, lockedSeats.getSeatIds());
    }
}
