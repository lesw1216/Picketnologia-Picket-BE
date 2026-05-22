package com.picketlogia.picket.api.qna.dto.request;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.qna.model.Qna;
import com.picketlogia.picket.api.user.model.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QnaCreateRequest {
    private String title;
    private String contents;
    private Boolean isPrivate;
    private String password;
    private Long productId;

    public Qna toEntity(Long userIdx) {

        Product product = Product.builder()
                .idx(productId).build();

        User user = User.builder()
                .idx(userIdx).build();
        return Qna.builder()
                .title(title)
                .contents(contents)
                .isPrivate(isPrivate)
                .product(product)
                .user(user)
                .password(password)
                .isDeleted(false)
                .build();
    }
}
