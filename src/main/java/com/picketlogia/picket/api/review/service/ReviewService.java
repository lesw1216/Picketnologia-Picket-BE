package com.picketlogia.picket.api.review.service;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.product.repository.ProductRepository;
import com.picketlogia.picket.api.reservation.service.ReservationService;
import com.picketlogia.picket.api.review.dto.request.ReviewRegisterRequest;
import com.picketlogia.picket.api.review.dto.result.ReviewListResult;
import com.picketlogia.picket.api.review.dto.result.ReviewPageResult;
import com.picketlogia.picket.api.review.model.entity.Review;
import com.picketlogia.picket.api.review.repository.ReviewRepository;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationService reservationService;
    private final ProductRepository productRepository;

    /**
     * 예매자 검증 후 리뷰를 저장하고 상품의 평점·리뷰 수 통계를 갱신한다.
     *
     * @param dto     리뷰 등록 요청
     * @param userIdx 작성자 ID
     * @throws BaseException 해당 상품을 구매한 적이 없을 때
     */
    @Transactional
    public void save(ReviewRegisterRequest dto, Long userIdx) {

        if (!reservationService.hasPurchasedProduct(userIdx, dto.getProductId())) {
            throw BaseException.from(BaseResponseStatus.ORDERS_NOT_ORDERED);
        }

        reviewRepository.save(dto.toEntity(userIdx));

        updateProductReviewStats(dto.getProductId());
    }

    private void updateProductReviewStats(Long productId) {

        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        Long reviewCount = reviewRepository.countByProductId(productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.PRODUCT_NOT_FOUND));

        product.updateReviewRating(averageRating != null ? averageRating : 0.0);
        product.updateReviewCount(reviewCount);
        productRepository.save(product);
    }

    /**
     * 사용자가 작성한 리뷰를 전체 조회한다.
     *
     * @param userIdx 사용자 ID
     * @return 작성된 리뷰 목록
     */
    public List<ReviewListResult> listByUser(Long userIdx) {

        List<Review> result = reviewRepository.findByUserIdx(userIdx);

        return result.stream().map(ReviewListResult::from).toList();
    }

    /**
     * 모든 리뷰를 작성자·상품 정보와 함께 조회한다.
     *
     * @return 리뷰 목록
     */
    public List<ReviewListResult> list() {

        List<Review> result = reviewRepository.findAllWithAllDetails();

        return result.stream().map(ReviewListResult::from).toList();
    }

    /**
     * 상품별 리뷰를 페이지 단위로 조회하고 평균 평점을 포함해 반환한다.
     *
     * @param page      페이지 번호 (0부터)
     * @param size      페이지 크기
     * @param productId 조회 대상 상품 ID
     * @return 페이지 결과
     */
    public ReviewPageResult listpaging(Integer page, Integer size, Long productId) {

        Page<Review> result = reviewRepository.findByProductIdx(productId, PageRequest.of(page, size));
        Double averageRating = reviewRepository.findAverageRating();

        return ReviewPageResult.from(result, averageRating);
    }

    /**
     * 사용자의 지정 기간 내 작성 리뷰를 조회한다.
     *
     * @param userIdx      사용자 ID
     * @param startDateStr 시작일 (yyyy-MM-dd)
     * @param endDateStr   종료일 (yyyy-MM-dd)
     * @return 기간 내 리뷰 목록
     */
    public List<ReviewListResult> listByUserAndDateRange(Long userIdx, String startDateStr, String endDateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDateStr, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX);

        List<Review> result = reviewRepository.findByUserIdxAndCreatedAtBetween(userIdx, startDateTime, endDateTime);

        return result.stream().map(ReviewListResult::from).toList();
    }
}
