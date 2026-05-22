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

    @GetMapping()
    public ResponseEntity<BaseResponse<RegionResults>> getRegions() {

        RegionResults regions = regionService.findAll();
        return ResponseEntity.ok(BaseResponse.success(regions));
    }
}
