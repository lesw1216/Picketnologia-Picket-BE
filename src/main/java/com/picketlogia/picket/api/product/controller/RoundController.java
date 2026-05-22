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

    @GetMapping("/date")
    public ResponseEntity<BaseResponse<RoundDatesResult>> getAllRound(@RequestParam(name = "product") Long productIdx) {

        RoundDatesResult allRoundDate = roundService.findAllByProductIdx(productIdx);
        return ResponseEntity.ok(BaseResponse.success(allRoundDate));
    }

    @GetMapping("/time")
    public ResponseEntity<BaseResponse<RoundTimesResult>> getRounds(@RequestParam(name = "date") Long roundDateIdx) {

        RoundTimesResult times = roundService.findRoundTimesByRoundDate(roundDateIdx);
        return ResponseEntity.ok(BaseResponse.success(times));
    }
}
