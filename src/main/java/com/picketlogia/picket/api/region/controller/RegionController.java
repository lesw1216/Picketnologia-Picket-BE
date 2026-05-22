package com.picketlogia.picket.api.region.controller;

import com.picketlogia.picket.api.region.dto.result.RegionResults;
import com.picketlogia.picket.api.region.service.RegionService;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    /**
     * 등록된 지역 목록을 전체 조회한다.
     *
     * @return 지역 목록을 담은 표준 응답
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<RegionResults>> getRegions() {

        RegionResults regions = regionService.findAll();

        return ResponseEntity.ok(BaseResponse.success(regions));
    }
}
