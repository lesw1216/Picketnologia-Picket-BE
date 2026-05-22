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

    @Transactional
    public void save(ReviewRegisterRequest dto, Long userIdx) {
        if (!reservationService.hasPurchasedProduct(userIdx, dto.getProductId())) {
            throw new BaseException("예매자만 리뷰작성이 가능합니다.", BaseResponseStatus.ORDERS_NOT_ORDERED);
        }
        reviewRepository.save(dto.toEntity(userIdx));

        updateProductReviewStats(dto.getProductId());
    }

    private void updateProductReviewStats(Long productId) {
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        Long reviewCount = reviewRepository.countByProductId(productId);

        Product product = productRepository.findById(productId)
                .orElseThrow();

        product.updateReviewRating(averageRating != null ? averageRating : 0.0);
        product.updateReviewCount(reviewCount);
        productRepository.save(product);
    }


    public List<ReviewListResult> listByUser(Long userIdx) {
        List<Review> result = reviewRepository.findByUserIdx(userIdx);
        return result.stream().map(ReviewListResult::from).toList();
    }


    public List<ReviewListResult> list() {

        List<Review> result = reviewRepository.findAllWithAllDetails();

        return result.stream().map(ReviewListResult::from).toList();
    }

    public ReviewPageResult listpaging(Integer page, Integer size, Long productId) {

        Page<Review> result = reviewRepository.findByProductIdx(productId, PageRequest.of(page, size));
        Double averageRating = reviewRepository.findAverageRating();

        return ReviewPageResult.from(result, averageRating);
    }

    public List<ReviewListResult> listByUserAndDateRange(Long userIdx, String startDateStr, String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDateStr, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX);

        List<Review> result = reviewRepository.findByUserIdxAndCreatedAtBetween(userIdx, startDateTime, endDateTime);

        return result.stream().map(ReviewListResult::from).toList();
    }

}
