package com.picketlogia.picket.api.region.controller;

import com.picketlogia.picket.api.region.dto.result.RegionResults;
import com.picketlogia.picket.api.region.service.RegionService;
import com.picketlogia.picket.common.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
@Tag(name="지역 조회 기능")
public class RegionController {

    private final RegionService regionService;

    @Operation(
            summary = "전체 지역 조회 기능 ",
            description = "등록된 모든 지역 정보 조회한다."
    )
    @GetMapping()
    public ResponseEntity<BaseResponse<RegionResults>> getRegions() {

        RegionResults regions = regionService.findAll();
        return ResponseEntity.ok(BaseResponse.success(regions));
    }
}