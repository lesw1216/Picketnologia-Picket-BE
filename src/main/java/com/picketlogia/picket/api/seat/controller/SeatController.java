package com.picketlogia.picket.api.seat.controller;

import com.picketlogia.picket.api.seat.dto.response.SeatInfoResponse;
import com.picketlogia.picket.api.seat.dto.result.SeatInfoResult;
import com.picketlogia.picket.api.seat.service.SeatInfoService;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seat-info")
@RequiredArgsConstructor
public class SeatController {

    private final SeatInfoService seatInfoService;

    /**
     * 상품과 회차 기준 좌석 등급·좌석 배치·예약 상태를 한 번에 조회한다.
     *
     * @param productIdx   조회할 공연 상품 ID
     * @param roundTimeIdx 조회할 회차 시간 ID
     * @return 좌석 정보가 담긴 표준 응답
     */
    @GetMapping
    public ResponseEntity<BaseResponse<SeatInfoResponse>> getSeatInfo(@RequestParam("product") Long productIdx,
                                                                      @RequestParam("roundTime") Long roundTimeIdx) {

        SeatInfoResult seatInfoResult = seatInfoService.findSeatInfo(productIdx, roundTimeIdx);

        return ResponseEntity.ok(BaseResponse.success(SeatInfoResponse.from(seatInfoResult)));
    }
}
