package com.picketlogia.picket.api.review.dto.result;

import com.picketlogia.picket.api.review.model.entity.Review;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class ReviewPageResult {
    private List<ReviewListResult> reviewDtoLists;
    private Integer totalPages;
    private Long totalCount;
    private Integer currentPage;
    private Integer currentSize;
    private Double totalRating;

    public static ReviewPageResult from(List<Review> entityList) {
        return ReviewPageResult.builder()
                .reviewDtoLists(entityList.stream().map(ReviewListResult::from).toList())
                .build();
    }

    public static ReviewPageResult from(Page<Review> pageResult, Double averageRating) {
        return ReviewPageResult.builder()
                .totalPages(pageResult.getTotalPages())
                .totalCount(pageResult.getTotalElements())
                .currentPage(pageResult.getPageable().getPageNumber())
                .currentSize(pageResult.getPageable().getPageSize())
                .totalRating(averageRating)
                .reviewDtoLists(pageResult.getContent().stream().map(ReviewListResult::from).toList())
                .build();
    }
}
