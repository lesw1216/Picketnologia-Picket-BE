package com.picketlogia.picket.api.product.dto.result;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.seat.dto.response.SeatGradeResponse;
import com.picketlogia.picket.api.seat.dto.result.SeatGradeResult;
import com.picketlogia.picket.utils.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProductDetailResult {
    private Long idx;
    private String name; // 상품이름
    private String rating; // 관람등급
    private String venueName; // 공연장명
    private String venueAddress; // 공연장 주소
    private LocalDate startDate; // 공연 시작일
    private LocalDate endDate; // 공연 종료일
    private Integer runningTime; // 러닝타임
    private String posterUrl; // 포스터 이미지 경로 (파일명)
    private String description; // 설명
    private Double reviewRating;
    private Integer reviewCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime openDateFormat;
    private LocalDateTime openDate;
    private List<SeatGradeResponse> seatGrades;

    public static ProductDetailResult from(Product product) {
        return ProductDetailResult.builder()
                .idx(product.getIdx())
                .name(product.getName())
                .rating(product.getRating())
                .venueName(product.getVenueName())
                .venueAddress(product.getVenueAddress())
                .startDate(product.getStartDate())
                .endDate(product.getEndDate())
                .openDateFormat(product.getOpenDate())
                .openDate(product.getOpenDate())
                .runningTime(product.getRunningTime())
                .description(product.getDescription())
                .reviewCount(product.getReviewCount())
                .reviewRating(product.getReviewRating())
                .posterUrl(product.getProductImage().getFileName())
                .seatGrades(
                        product.getSeatGrades().stream().map(
                                seatGrade -> SeatGradeResponse.from(SeatGradeResult.from(seatGrade))
                        ).toList()
                )
                .build();
    }
}
