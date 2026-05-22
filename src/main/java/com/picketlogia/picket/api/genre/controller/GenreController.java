package com.picketlogia.picket.api.genre.controller;

import com.picketlogia.picket.api.genre.dto.response.GenreListResponse;
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

    /**
     * 등록된 장르 전체를 조회한다.
     *
     * @return 장르 목록을 담은 표준 응답
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<Object>> getList() {

        GenreListResponse genres = genreService.findAll();

        return ResponseEntity.ok(BaseResponse.success(genres));
    }
}
