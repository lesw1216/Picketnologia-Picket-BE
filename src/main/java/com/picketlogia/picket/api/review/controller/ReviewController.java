package com.picketlogia.picket.api.review.controller;

import com.picketlogia.picket.api.review.dto.request.ReviewRegisterRequest;
import com.picketlogia.picket.api.review.dto.result.ReviewListResult;
import com.picketlogia.picket.api.review.dto.result.ReviewPageResult;
import com.picketlogia.picket.api.review.service.ReviewService;
import com.picketlogia.picket.api.user.dto.request.UserAuthRequest;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 예매자 검증 후 리뷰를 등록한다.
     *
     * @param dto      리뷰 등록 요청
     * @param userAuth 작성자 인증 정보
     * @return 등록 성공 메시지를 담은 표준 응답
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<String>> register(@RequestBody ReviewRegisterRequest dto,
                                                         @AuthenticationPrincipal UserAuthRequest userAuth) {

        reviewService.save(dto, userAuth.getIdx());

        return ResponseEntity.ok(BaseResponse.success("리뷰저장성공"));
    }

    /**
     * 모든 리뷰를 작성자·상품 정보와 함께 조회한다.
     *
     * @return 리뷰 목록을 담은 표준 응답
     */
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<List<ReviewListResult>>> list() {

        List<ReviewListResult> response = reviewService.findAll();

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /**
     * 로그인 사용자가 지정 기간 동안 작성한 리뷰를 조회한다.
     *
     * @param userAuth  요청자 인증 정보
     * @param startDate 시작일 (yyyy-MM-dd)
     * @param endDate   종료일 (yyyy-MM-dd)
     * @return 기간 내 리뷰 목록을 담은 표준 응답
     */
    @GetMapping("/userReviewList")
    public ResponseEntity<BaseResponse<List<ReviewListResult>>> getUserReviewsByDate(@AuthenticationPrincipal UserAuthRequest userAuth,
                                                                                     @RequestParam("startDate") String startDate,
                                                                                     @RequestParam("endDate") String endDate) {

        List<ReviewListResult> response = reviewService.findAllByUserIdxAndCreatedAtBetween(userAuth.getIdx(), startDate, endDate);

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /**
     * 상품별 리뷰를 페이지 단위로 조회한다 (평균 평점 포함).
     *
     * @param page      페이지 번호 (0부터)
     * @param size      페이지 크기
     * @param productId 조회 대상 상품 ID
     * @return 페이지 결과를 담은 표준 응답
     */
    @GetMapping("/listPaging")
    public ResponseEntity<BaseResponse<ReviewPageResult>> listPaging(@RequestParam Integer page,
                                                                     @RequestParam Integer size,
                                                                     @RequestParam Long productId) {

        ReviewPageResult response = reviewService.findAllPagedByProductIdx(page, size, productId);

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
