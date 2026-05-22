package com.picketlogia.picket.api.sortoption.controller;

import com.picketlogia.picket.api.sortoption.model.SortOption;
import com.picketlogia.picket.api.sortoption.model.SortOptionList;
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
    public ResponseEntity<BaseResponse<SortOptionList>> getSortOptions() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        SortOptionList.from(
                                Arrays.stream(SortOption.values()).toList()
                        )
                )
        );
    }
}
