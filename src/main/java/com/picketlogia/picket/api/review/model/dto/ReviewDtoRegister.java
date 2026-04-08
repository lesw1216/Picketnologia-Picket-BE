package com.picketlogia.picket.api.review.model.dto;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.review.model.entity.Review;
import com.picketlogia.picket.api.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewDtoRegister {

    @Schema(description = "리뷰의 별점", example = "3", minimum = "1", maximum = "5")
    @NotNull(message="별점을 선택해 주세요")
    private Integer rating;

    @Schema(description = "리뷰 내용 ", example = "정말 좋은 공연이었습니다.")
    @NotBlank(message="리뷰내용을 입력해주세요")
    @Lob
    private String comment;

    @Schema(description = "리뷰 작성한 상품ID", example = "1")
    private Long productId;

    public Review toEntity(Long userIdx){
        Product product = Product.builder()
                .idx(productId).build();

        User user = User.builder()
                .idx(userIdx).build();

        Review entity = Review.builder()

                .rating(rating)
                .comment(comment)
                .product(product)
                .user(user)
                .build();
        return entity;
    }

}
