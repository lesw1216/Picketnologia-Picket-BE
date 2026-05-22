package com.picketlogia.picket.api.qna.controller;

import com.picketlogia.picket.api.qna.dto.request.QnaAnswerCreateRequest;
import com.picketlogia.picket.api.qna.dto.request.QnaAnswerUpdateRequest;
import com.picketlogia.picket.api.qna.dto.request.QnaCreateRequest;
import com.picketlogia.picket.api.qna.dto.request.QnaUpdateRequest;
import com.picketlogia.picket.api.qna.dto.response.QnaAnswerResponse;
import com.picketlogia.picket.api.qna.dto.response.QnaResponse;
import com.picketlogia.picket.api.qna.dto.result.QnaPageResult;
import com.picketlogia.picket.api.qna.service.QnaService;
import com.picketlogia.picket.api.user.dto.request.UserAuthRequest;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnaController {

    private final QnaService qnaService;


    @PostMapping("/qna_create_post")
    public ResponseEntity createQna(@RequestBody QnaCreateRequest dto, @AuthenticationPrincipal UserAuthRequest userAuth) {

        qnaService.save(dto, userAuth.getIdx());
        return ResponseEntity.status(200).body("리뷰저장성공");
    }


    @GetMapping("/qna_get_all_posts")
    public ResponseEntity<List<QnaResponse>> getAllQna() {
        return ResponseEntity.ok(qnaService.findAllQna());
    }


    @GetMapping("/{qnaIdx}")
    public ResponseEntity<QnaResponse> getQnaByIdx(@PathVariable Long qnaIdx) {
        return ResponseEntity.ok(qnaService.findQnaByIdx(qnaIdx));
    }

    @PutMapping("/{qnaIdx}")
    public ResponseEntity<QnaResponse> updateQna(@PathVariable Long qnaIdx, @RequestBody QnaUpdateRequest request) {
        return ResponseEntity.ok(qnaService.updateQna(qnaIdx, request));
    }

    @DeleteMapping("/{qnaIdx}")
    public ResponseEntity<Void> deleteQna(@PathVariable Long qnaIdx) {
        qnaService.deleteQna(qnaIdx);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{qnaIdx}/answers")
    public ResponseEntity<QnaAnswerResponse> createAnswer(
            @PathVariable Long qnaIdx,
            @RequestBody QnaAnswerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(qnaService.createAnswer(qnaIdx, request));
    }


    @PutMapping("/{qnaIdx}/answers/{answerIdx}")
    public ResponseEntity<QnaAnswerResponse> updateAnswer(
            @PathVariable Long qnaIdx,
            @PathVariable Long answerIdx,
            @RequestBody QnaAnswerUpdateRequest request) {
        return ResponseEntity.ok(qnaService.updateAnswer(qnaIdx, answerIdx, request));
    }

    @DeleteMapping("/{qnaIdx}/answers/{answerIdx}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long qnaIdx,
            @PathVariable Long answerIdx) {
        qnaService.deleteAnswer(qnaIdx, answerIdx);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/userQnaList")
    public ResponseEntity<BaseResponse<List<QnaResponse>>> getUserReviewsByDate(
            @AuthenticationPrincipal UserAuthRequest userAuth,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ) {
        List<QnaResponse> response = qnaService.listByUserAndDateRange(userAuth.getIdx(), startDate, endDate);
        return ResponseEntity.ok(BaseResponse.success(response));
    }


    @GetMapping("/qnaPaging")
    public ResponseEntity<BaseResponse<QnaPageResult>> listPaging(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam Long productId) {
        QnaPageResult response = qnaService.pnaPaging(page, size, productId);

        return ResponseEntity.status(200).body(BaseResponse.success(response));
    }
}
