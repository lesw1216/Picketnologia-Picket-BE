package com.picketlogia.picket.api.review.dto.request;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.review.model.entity.Review;
import com.picketlogia.picket.api.user.model.entity.User;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRegisterRequest {

    @NotNull(message = "별점을 선택해 주세요")
    private Integer rating;

    @NotBlank(message = "리뷰내용을 입력해주세요")
    @Lob
    private String comment;

    private Long productId;

    public Review toEntity(Long userIdx) {
        Product product = Product.builder()
                .idx(productId).build();

        User user = User.builder()
                .idx(userIdx).build();

        return Review.builder()
                .rating(rating)
                .comment(comment)
                .product(product)
                .user(user)
                .build();
    }
}
