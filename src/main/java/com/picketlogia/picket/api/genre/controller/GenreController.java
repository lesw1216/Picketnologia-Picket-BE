package com.picketlogia.picket.api.genre.controller;

import com.picketlogia.picket.api.genre.dto.result.GenreListResponse;
import com.picketlogia.picket.api.genre.service.GenreService;
import com.picketlogia.picket.common.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
@Tag(name ="공연 전체 조회 기능")
public class GenreController {

    private final GenreService genreService;

    // 전체 조회
    @Operation(
            summary = "공연 전체 조회 ",
            description = "등록된 공연 전체를 조회한다."
    )
    @GetMapping()
    public ResponseEntity<BaseResponse<Object>> getList() {
        GenreListResponse genres = genreService.findAll();

        return ResponseEntity.ok(BaseResponse.success(genres));
    }
}
