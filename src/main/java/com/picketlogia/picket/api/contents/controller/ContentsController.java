package com.picketlogia.picket.api.contents.controller;

import com.picketlogia.picket.api.contents.dto.ContentsResponse;
import com.picketlogia.picket.api.contents.service.ContentsService;
import com.picketlogia.picket.common.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name ="공연 장르 조회 기능")
public class ContentsController {

    private final ContentsService contentsService;

    @Operation(
            summary = "공연 장르 조회 - RequestParam에 맞게 ",
            description = "다양한 공연 장르 중 RequestParam에 맞는 공연들만 조회"
    )
    @GetMapping("/contents/{genre}")
    public ResponseEntity<BaseResponse<ContentsResponse>> getContents(@PathVariable String genre) {

        ContentsResponse contents = contentsService.findContents(genre);
        return ResponseEntity.ok(BaseResponse.success(contents));
    }
}
