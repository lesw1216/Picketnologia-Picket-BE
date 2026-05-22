package com.picketlogia.picket.api.product.controller;

import com.picketlogia.picket.api.product.dto.result.RoundDatesResult;
import com.picketlogia.picket.api.product.dto.result.RoundTimesResult;
import com.picketlogia.picket.api.product.service.RoundService;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/round")
public class RoundController {

    private final RoundService roundService;

    /**
     * 특정 공연의 오늘 이후 회차 날짜 목록을 조회한다.
     *
     * @param productIdx 조회할 공연 상품 ID
     * @return 회차 날짜 목록을 담은 표준 응답
     */
    @GetMapping("/date")
    public ResponseEntity<BaseResponse<RoundDatesResult>> getAllRound(@RequestParam(name = "product") Long productIdx) {

        RoundDatesResult allRoundDate = roundService.findAllByProductIdx(productIdx);

        return ResponseEntity.ok(BaseResponse.success(allRoundDate));
    }

    /**
     * 선택한 회차 날짜의 시간 목록을 조회한다. 오늘 날짜이면 현재 시간 이후만 반환한다.
     *
     * @param roundDateIdx 회차 날짜 ID
     * @return 회차 시간 목록을 담은 표준 응답
     */
    @GetMapping("/time")
    public ResponseEntity<BaseResponse<RoundTimesResult>> getRounds(@RequestParam(name = "date") Long roundDateIdx) {

        RoundTimesResult times = roundService.findRoundTimesByRoundDate(roundDateIdx);

        return ResponseEntity.ok(BaseResponse.success(times));
    }
}
