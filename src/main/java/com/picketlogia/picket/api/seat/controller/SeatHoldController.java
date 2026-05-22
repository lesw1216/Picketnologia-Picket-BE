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

    @GetMapping
    public ResponseEntity<BaseResponse<Map<Object, Object>>> getHeldSeats(@PathVariable Long roundTimeId) {

        Map<Object, Object> heldSeats = seatHoldService.getHeldSeats(roundTimeId);
        return ResponseEntity.ok(BaseResponse.success(heldSeats));

    }

    @DeleteMapping
    public void releaseSeats(@PathVariable Long roundTimeId, @RequestBody LockedSeatsRequest lockedSeatsRequest) {

        seatHoldService.releaseHeldSeats(
                ReleaseHeldSeatsCommand.builder()
                        .roundTimeId(roundTimeId)
                        .seatIds(lockedSeatsRequest.getSeatIds())
                        .build()
        );
        messagingTemplate.convertAndSend("/topic/seats/map/" + roundTimeId, lockedSeatsRequest.getSeatIds());
    }
}
