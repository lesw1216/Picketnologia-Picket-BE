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

    /**
     * 새 문의글을 등록한다.
     *
     * @param dto      문의글 등록 요청
     * @param userAuth 작성자 인증 정보
     * @return 등록 성공 메시지를 담은 표준 응답
     */
    @PostMapping("/qna_create_post")
    public ResponseEntity<BaseResponse<String>> createQna(@RequestBody QnaCreateRequest dto,
                                                          @AuthenticationPrincipal UserAuthRequest userAuth) {

        qnaService.save(dto, userAuth.getIdx());

        return ResponseEntity.ok(BaseResponse.success("문의글 저장 성공"));
    }

    /**
     * 삭제되지 않은 문의글을 전부 조회한다.
     *
     * @return 문의글 목록을 담은 표준 응답
     */
    @GetMapping("/qna_get_all_posts")
    public ResponseEntity<BaseResponse<List<QnaResponse>>> getAllQna() {

        return ResponseEntity.ok(BaseResponse.success(qnaService.findAllQna()));
    }

    /**
     * ID로 단일 문의글을 조회한다.
     *
     * @param qnaIdx 조회할 문의글 ID
     * @return 문의글 응답을 담은 표준 응답
     */
    @GetMapping("/{qnaIdx}")
    public ResponseEntity<BaseResponse<QnaResponse>> getQnaByIdx(@PathVariable Long qnaIdx) {

        return ResponseEntity.ok(BaseResponse.success(qnaService.findQnaByIdx(qnaIdx)));
    }

    /**
     * 비밀번호 검증 후 문의글 본문을 수정한다.
     *
     * @param qnaIdx  수정할 문의글 ID
     * @param request 수정 요청 정보
     * @return 수정된 문의글을 담은 표준 응답
     */
    @PutMapping("/{qnaIdx}")
    public ResponseEntity<BaseResponse<QnaResponse>> updateQna(@PathVariable Long qnaIdx,
                                                               @RequestBody QnaUpdateRequest request) {

        return ResponseEntity.ok(BaseResponse.success(qnaService.updateQna(qnaIdx, request)));
    }

    /**
     * 문의글을 소프트 삭제한다.
     *
     * @param qnaIdx 삭제할 문의글 ID
     * @return 빈 본문의 표준 응답 (204)
     */
    @DeleteMapping("/{qnaIdx}")
    public ResponseEntity<BaseResponse<Void>> deleteQna(@PathVariable Long qnaIdx) {

        qnaService.deleteQna(qnaIdx);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(BaseResponse.success(null));
    }

    /**
     * 문의글에 답변을 새로 등록한다.
     *
     * @param qnaIdx  답변을 달 문의글 ID
     * @param request 답변 등록 요청
     * @return 등록된 답변을 담은 표준 응답 (201)
     */
    @PostMapping("/{qnaIdx}/answers")
    public ResponseEntity<BaseResponse<QnaAnswerResponse>> createAnswer(@PathVariable Long qnaIdx,
                                                                        @RequestBody QnaAnswerCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(qnaService.createAnswer(qnaIdx, request)));
    }

    /**
     * 문의글에 속한 답변 본문을 수정한다.
     *
     * @param qnaIdx    답변이 속한 문의글 ID
     * @param answerIdx 수정할 답변 ID
     * @param request   답변 수정 요청
     * @return 수정된 답변을 담은 표준 응답
     */
    @PutMapping("/{qnaIdx}/answers/{answerIdx}")
    public ResponseEntity<BaseResponse<QnaAnswerResponse>> updateAnswer(@PathVariable Long qnaIdx,
                                                                        @PathVariable Long answerIdx,
                                                                        @RequestBody QnaAnswerUpdateRequest request) {

        return ResponseEntity.ok(BaseResponse.success(qnaService.updateAnswer(qnaIdx, answerIdx, request)));
    }

    /**
     * 문의글에 속한 답변을 소프트 삭제한다.
     *
     * @param qnaIdx    답변이 속한 문의글 ID
     * @param answerIdx 삭제할 답변 ID
     * @return 빈 본문의 표준 응답 (204)
     */
    @DeleteMapping("/{qnaIdx}/answers/{answerIdx}")
    public ResponseEntity<BaseResponse<Void>> deleteAnswer(@PathVariable Long qnaIdx,
                                                           @PathVariable Long answerIdx) {

        qnaService.deleteAnswer(qnaIdx, answerIdx);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(BaseResponse.success(null));
    }

    /**
     * 사용자가 지정 기간 동안 작성한 문의글을 조회한다.
     *
     * @param userAuth  요청자 인증 정보
     * @param startDate 시작일 (yyyy-MM-dd)
     * @param endDate   종료일 (yyyy-MM-dd)
     * @return 기간 내 문의글 목록을 담은 표준 응답
     */
    @GetMapping("/userQnaList")
    public ResponseEntity<BaseResponse<List<QnaResponse>>> getUserReviewsByDate(@AuthenticationPrincipal UserAuthRequest userAuth,
                                                                                @RequestParam("startDate") String startDate,
                                                                                @RequestParam("endDate") String endDate) {

        List<QnaResponse> response = qnaService.listByUserAndDateRange(userAuth.getIdx(), startDate, endDate);

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /**
     * 상품별 문의글을 페이지 단위로 조회한다.
     *
     * @param page      페이지 번호 (0부터)
     * @param size      페이지 크기
     * @param productId 조회 대상 상품 ID
     * @return 페이징 결과를 담은 표준 응답
     */
    @GetMapping("/qnaPaging")
    public ResponseEntity<BaseResponse<QnaPageResult>> listPaging(@RequestParam Integer page,
                                                                  @RequestParam Integer size,
                                                                  @RequestParam Long productId) {

        QnaPageResult response = qnaService.pnaPaging(page, size, productId);

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
