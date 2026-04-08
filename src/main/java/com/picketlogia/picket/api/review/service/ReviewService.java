package com.picketlogia.picket.api.review.service;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.product.repository.ProductRepository;
import com.picketlogia.picket.api.reservation.service.ReservationService;
import com.picketlogia.picket.api.review.model.dto.ReviewDtoList;
import com.picketlogia.picket.api.review.model.dto.ReviewDtoRegister;
import com.picketlogia.picket.api.review.model.dto.ReviewList;
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
    public void save(ReviewDtoRegister dto, Long userIdx) {
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



    public List<ReviewDtoList> listByUser(Long userIdx) {
         List<Review> result = reviewRepository.findByUserIdx(userIdx);
       return result.stream().map(ReviewDtoList::from).toList();
  }


    public List<ReviewDtoList> list() {

    List<Review> result = reviewRepository.findAllWithAllDetails();

//    List<Review> result = reviewRepository.findAllWithUserAndProduct();


    return result.stream().map(ReviewDtoList::from).toList();
}

    public ReviewList listpaging(Integer page, Integer size ,Long productId) {

        Page<Review> result = reviewRepository.findByProductIdx(productId,PageRequest.of(page,size)); // 페이지네이션이 필요하면 사용
//        Slice<Board> result = boardRepository.findAll(PageRequest.of(page,size)); // 페이지네이션이 필요없으면 사용
        Double averageRating = reviewRepository.findAverageRating();

        return ReviewList.from(result, averageRating);
    }

    public List<ReviewDtoList> listByUserAndDateRange(Long userIdx, String startDateStr, String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDateStr, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX);

        List<Review> result = reviewRepository.findByUserIdxAndCreatedAtBetween(userIdx, startDateTime, endDateTime);

        return result.stream().map(ReviewDtoList::from).toList();
    }

}
