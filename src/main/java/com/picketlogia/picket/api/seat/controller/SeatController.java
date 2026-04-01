package com.picketlogia.picket.api.seat.controller;

import com.picketlogia.picket.api.seat.dto.response.SeatInfoResponse;
import com.picketlogia.picket.api.seat.dto.result.SeatInfoResult;
import com.picketlogia.picket.api.seat.service.SeatInfoService;
import com.picketlogia.picket.common.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seat-info")
@RequiredArgsConstructor
@Tag(name = "좌석 정보 조회 기능 ")
public class SeatController {

    private final SeatInfoService seatInfoService;

    @Operation(
            summary = "좌석 정보 조회",
            description = "조건에 맞는 공연의 좌석 정보를 조회한다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<SeatInfoResponse>> getSeatInfo(@RequestParam("product") Long productIdx,
                                                                      @RequestParam("roundTime") Long roundTimeIdx) {

        SeatInfoResult seatInfoResult = seatInfoService.findSeatInfo(productIdx, roundTimeIdx);

        return ResponseEntity.ok(BaseResponse.success(SeatInfoResponse.from(seatInfoResult)));

    }
}
