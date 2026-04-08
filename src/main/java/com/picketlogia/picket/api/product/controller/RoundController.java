package com.picketlogia.picket.api.product.controller;

import com.picketlogia.picket.api.product.dto.result.RoundDatesResult;
import com.picketlogia.picket.api.product.dto.result.RoundTimesResult;
import com.picketlogia.picket.api.product.service.RoundService;
import com.picketlogia.picket.common.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "특정 공연의 회차일을 조회한다.",
            description = "특정 공연의 회차일을 조회한다. 이 때, 오늘 날짜 이전의 회차일 목록은 조회하지 않는다."
    )
    @GetMapping("/date")
    public ResponseEntity<BaseResponse<RoundDatesResult>> getAllRound(@RequestParam(name = "product") Long productIdx) {

        RoundDatesResult allRoundDate = roundService.findAllByProductIdx(productIdx);
        return ResponseEntity.ok(BaseResponse.success(allRoundDate));
    }

    @Operation(
            summary = "선택한 회차일의 회차 시간을 조회한다.",
            description = "선택한 회차일의 회차 시간을 조회한다. 이 때, 회차일이 오늘이라면 현재 시간 이후의 회차 시간만을 조회한다."
    )
    @GetMapping("/time")
    public ResponseEntity<BaseResponse<RoundTimesResult>> getRounds(@RequestParam(name = "date") Long roundDateIdx) {

        RoundTimesResult times = roundService.findRoundTimesByRoundDate(roundDateIdx);
        return ResponseEntity.ok(BaseResponse.success(times));
    }
}
