package com.picketlogia.picket.api.qna.controller;

import com.picketlogia.picket.api.qna.model.QnaDto;
import com.picketlogia.picket.api.qna.model.QnaList;
import com.picketlogia.picket.api.qna.service.QnaService;
import com.picketlogia.picket.api.user.dto.request.UserAuthRequest;
import com.picketlogia.picket.common.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qna")
@Tag(name ="QnA 기능")
public class QnaController {

    private final QnaService qnaService;


    @Operation(
            summary = "문의글 생성 ",
            description = "질문 내용 등록하는 기능"
    )
    @PostMapping("/qna_create_post")
    public ResponseEntity createQna(@RequestBody QnaDto.CreateRequest dto, @AuthenticationPrincipal UserAuthRequest userAuth) {

        qnaService.save(dto ,userAuth.getIdx());
        return ResponseEntity.status(200).body("리뷰저장성공");
    }
//    @PostMapping("/register")
//    public ResponseEntity register(@RequestBody ReviewDtoRegister dto, @AuthenticationPrincipal UserAuth userAuth) {
//        reviewService.save(dto, userAuth.getIdx());
//
//        return ResponseEntity.status(200).body("리뷰저장성공");
//
//    }


    @Operation(
            summary = "문의글 전체 조회 ",
            description = "저장되어 있는 문의글을 전체 조회한다."
    )
    @GetMapping("/qna_get_all_posts")
    public ResponseEntity<List<QnaDto.Response>> getAllQna() {
        return ResponseEntity.ok(qnaService.findAllQna());
    }


    @Operation(
            summary = "문의글 조회 ",
            description = "조건에 맞는 문의글을 조회한다."
    )
    @GetMapping("/{qnaIdx}")
    public ResponseEntity<QnaDto.Response> getQnaByIdx(@PathVariable Long qnaIdx) {
        return ResponseEntity.ok(qnaService.findQnaByIdx(qnaIdx));
    }

    @Operation(
            summary = "문의글 수정 ",
            description = "조건에 맞는 문의글을 수정한다"
    )
    @PutMapping("/{qnaIdx}")
    public ResponseEntity<QnaDto.Response> updateQna(@PathVariable Long qnaIdx, @RequestBody QnaDto.UpdateRequest request) {
        return ResponseEntity.ok(qnaService.updateQna(qnaIdx, request));
    }

    @Operation(
            summary = " 문의글 삭제 ",
            description = "조건에 맞는 문의글을 삭제한다"
    )
    @DeleteMapping("/{qnaIdx}")
    public ResponseEntity<Void> deleteQna(@PathVariable Long qnaIdx) {
        qnaService.deleteQna(qnaIdx);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "문의글 답변 등록 ",
            description = "조건에 맞는 문의글에  답변을 등록한다"
    )
    @PostMapping("/{qnaIdx}/answers")
    public ResponseEntity<QnaDto.AnswerResponse> createAnswer(
            @PathVariable Long qnaIdx,
            @RequestBody QnaDto.CreateAnswerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(qnaService.createAnswer(qnaIdx, request));
    }


    @Operation(
            summary = "문의글 답변 수정 ",
            description = "조건에 맞는 문의글에 대한 답변을 수정한다"
    )
    @PutMapping("/{qnaIdx}/answers/{answerIdx}")
    public ResponseEntity<QnaDto.AnswerResponse> updateAnswer(
            @PathVariable Long qnaIdx,
            @PathVariable Long answerIdx,
            @RequestBody QnaDto.UpdateAnswerRequest request) {
        return ResponseEntity.ok(qnaService.updateAnswer(qnaIdx, answerIdx, request));
    }

    @Operation(
            summary = "문의글 답변 삭제 ",
            description = "조건에 맞는 문의글에 대한 답변을 삭제한다"
    )
    @DeleteMapping("/{qnaIdx}/answers/{answerIdx}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long qnaIdx,
            @PathVariable Long answerIdx) {
        qnaService.deleteAnswer(qnaIdx, answerIdx);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/userQnaList")
    public ResponseEntity<BaseResponse<List<QnaDto.Response>>> getUserReviewsByDate(
            @AuthenticationPrincipal UserAuthRequest userAuth,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ) {
        List<QnaDto.Response> response = qnaService.listByUserAndDateRange(userAuth.getIdx(), startDate, endDate);
        return ResponseEntity.ok(BaseResponse.success(response));
    }


    @GetMapping("/qnaPaging")// 페이지 번호는 0번부터
    public ResponseEntity<BaseResponse<QnaList>> listPaging (
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam Long productId){
        QnaList response = qnaService.pnaPaging(page, size, productId);

        return ResponseEntity.status(200).body(BaseResponse.success(response));
    }

}

