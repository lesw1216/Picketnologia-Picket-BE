package com.picketlogia.picket.api.sortoption.controller;

import com.picketlogia.picket.api.sortoption.dto.result.SortOptionListResult;
import com.picketlogia.picket.api.sortoption.model.SortOption;
import com.picketlogia.picket.common.model.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/sort-options")
public class SortOptionController {

    @GetMapping()
    public ResponseEntity<BaseResponse<SortOptionListResult>> getSortOptions() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        SortOptionListResult.from(
                                Arrays.stream(SortOption.values()).toList()
                        )
                )
        );
    }
}
