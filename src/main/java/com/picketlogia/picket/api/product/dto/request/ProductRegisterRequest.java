package com.picketlogia.picket.api.product.dto.request;

import com.picketlogia.picket.api.genre.model.Genre;
import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.seat.dto.request.SeatGradeRequest;
import com.picketlogia.picket.api.seat.dto.request.SeatRequest;
import com.picketlogia.picket.api.user.model.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegisterRequest {

    private String name;           // 상품이름
    private String rating;         // 관람등급
    private String venueName;      // 공연장명
    private String venueAddress;   // 공연장 주소
    private LocalDate startDate;   // 공연 시작일
    private LocalDate endDate;     // 공연 종료일
    private Integer runningTime;   // 러닝타임
    private String description;    // 설명
    private String genre;           // 장르
    private LocalDateTime openDate;    // 오픈 예정일
    private PerformanceRoundRequest roundOption;
    private List<List<SeatRequest>> seatMap;
    private List<SeatGradeRequest> seatGrade;

    // DTO → Entity 변환
    public Product toEntity(Integer genreId, Long userIdx) {
        return Product.builder()
                .name(name)
                .rating(rating)
                .venueName(venueName)
                .venueAddress(venueAddress)
                .startDate(startDate)
                .endDate(endDate)
                .runningTime(runningTime)
                .description(description)
                .openDate(openDate)
                .genre(
                        Genre.builder().
                                idx(genreId)
                                .build()
                )
                .user(
                        User.builder().idx(userIdx).build()
                )
                .build();
    }

    public static ProductRegisterRequest fromEntity(Product product) {
        return ProductRegisterRequest.builder()
                .name(product.getName())
                .rating(product.getRating())
                .venueName(product.getVenueName())
                .venueAddress(product.getVenueAddress())
                .startDate(product.getStartDate())
                .endDate(product.getEndDate())
                .runningTime(product.getRunningTime())
                .description(product.getDescription())
                .build();
    }
}
