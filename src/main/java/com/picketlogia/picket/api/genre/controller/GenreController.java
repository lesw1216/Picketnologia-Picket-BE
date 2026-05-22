package com.picketlogia.picket.api.genre.controller;

import com.picketlogia.picket.api.genre.dto.result.GenreListResponse;
import com.picketlogia.picket.api.genre.service.GenreService;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping()
    public ResponseEntity<BaseResponse<Object>> getList() {
        GenreListResponse genres = genreService.findAll();

        return ResponseEntity.ok(BaseResponse.success(genres));
    }
}
