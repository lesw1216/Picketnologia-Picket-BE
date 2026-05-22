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

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody ReviewRegisterRequest dto, @AuthenticationPrincipal UserAuthRequest userAuth) {
        reviewService.save(dto, userAuth.getIdx());

        return ResponseEntity.status(200).body("리뷰저장성공");

    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<List<ReviewListResult>>> list() {
        List<ReviewListResult> response = reviewService.list();

        return ResponseEntity.status(200).body(BaseResponse.success(response));
    }

    @GetMapping("/userReviewList")
    public ResponseEntity<BaseResponse<List<ReviewListResult>>> getUserReviewsByDate(
            @AuthenticationPrincipal UserAuthRequest userAuth,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ) {
        List<ReviewListResult> response = reviewService.listByUserAndDateRange(userAuth.getIdx(), startDate, endDate);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/listPaging")
    public ResponseEntity<BaseResponse<ReviewPageResult>> listPaging(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam Long productId) {
        ReviewPageResult response = reviewService.listpaging(page, size, productId);

        return ResponseEntity.status(200).body(BaseResponse.success(response));
    }
}
