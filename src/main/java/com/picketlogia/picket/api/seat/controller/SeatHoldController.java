package com.picketlogia.picket.api.seat.controller;

import com.picketlogia.picket.api.seat.dto.command.ReleaseHeldSeatsCommand;
import com.picketlogia.picket.api.seat.dto.request.LockedSeatsRequest;
import com.picketlogia.picket.api.seat.service.SeatHoldService;
import com.picketlogia.picket.common.model.BaseResponse;
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

    /**
     * Redis에 임시 선점된 좌석 정보를 조회한다.
     *
     * @param roundTimeId 조회할 회차 시간 ID
     * @return 선점된 좌석 정보가 담긴 표준 응답
     */
    @GetMapping
    public ResponseEntity<BaseResponse<Map<Object, Object>>> getHeldSeats(@PathVariable Long roundTimeId) {

        Map<Object, Object> heldSeats = seatHoldService.getHeldSeats(roundTimeId);

        return ResponseEntity.ok(BaseResponse.success(heldSeats));
    }

    /**
     * 사용자가 임시 선점한 좌석을 해제하고 WebSocket으로 해제 사실을 브로드캐스트한다.
     *
     * @param roundTimeId        해제할 회차 시간 ID
     * @param lockedSeatsRequest 해제 대상 좌석 ID 목록
     * @return 빈 본문의 표준 응답
     */
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> releaseSeats(@PathVariable Long roundTimeId,
                                                           @RequestBody LockedSeatsRequest lockedSeatsRequest) {

        seatHoldService.releaseHeldSeats(
                ReleaseHeldSeatsCommand.builder()
                        .roundTimeId(roundTimeId)
                        .seatIds(lockedSeatsRequest.getSeatIds())
                        .build()
        );
        messagingTemplate.convertAndSend("/topic/seats/map/" + roundTimeId, lockedSeatsRequest.getSeatIds());

        return ResponseEntity.ok(BaseResponse.success(null));
    }
}
