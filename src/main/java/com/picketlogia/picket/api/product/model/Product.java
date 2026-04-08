package com.picketlogia.picket.api.product.model;

import com.picketlogia.picket.api.genre.model.Genre;
import com.picketlogia.picket.api.qna.model.Qna;
import com.picketlogia.picket.api.review.model.entity.Review;
import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.model.SeatGrade;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Table(name = "product", indexes = {
        @Index(name = "idx_product_genre_id", columnList = "genre_id")
    }
)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String name; // 상품이름
    private String rating; // 관람등급
    private String venueName; // 공연장명
    private String venueAddress; // 공연장 주소
    private LocalDate startDate; // 공연 시작일
    private LocalDate endDate; // 공연 종료일
    private LocalDateTime openDate; // 오픈 예정일
    private Integer runningTime; // 러닝타임
    private String description; // 설명

    @ColumnDefault("0")
    private Double reviewRating;

    @ColumnDefault("0")
    private Integer reviewCount;

    @ColumnDefault("0")
    private Long salesCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<Review> reviewList;

    @OneToMany(mappedBy = "product" , fetch = FetchType.LAZY)
    private List<Qna> qnaList;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private ProductImage productImage;

    // 일정 정보 관계 정립(1개의 상품에 N개의 일정)
    @OneToMany(mappedBy = "product" ,fetch = FetchType.LAZY)
    private List<RoundDate> roundDate;

    @OneToMany(mappedBy = "product")
    private List<Seat> seatList;

    @OneToMany(mappedBy = "product")
    private List<SeatGrade> seatGrades;

    public void updateReviewRating(Double reviewRating) {
        this.reviewRating = reviewRating;
    }

    public void updateReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount.intValue();
    }

    public void incrementSalesCount() {
        this.salesCount++;
    }

    public void decrementSalesCount() {
        this.salesCount--;
    }
}
