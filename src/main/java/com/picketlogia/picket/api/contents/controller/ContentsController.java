package com.picketlogia.picket.api.contents.controller;

import com.picketlogia.picket.api.contents.dto.ContentsResponse;
import com.picketlogia.picket.api.contents.service.ContentsService;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ContentsController {

    private final ContentsService contentsService;

    /**
     * 지정된 장르의 콘텐츠(공연 목록 + 오픈 예정 공연)를 조회한다.
     *
     * @param genre 장르 코드
     * @return 콘텐츠 응답을 담은 표준 응답
     */
    @GetMapping("/contents/{genre}")
    public ResponseEntity<BaseResponse<ContentsResponse>> getContents(@PathVariable String genre) {

        ContentsResponse contents = contentsService.findContents(genre);

        return ResponseEntity.ok(BaseResponse.success(contents));
    }
}
