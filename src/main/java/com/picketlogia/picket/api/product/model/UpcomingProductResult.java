package com.picketlogia.picket.api.product.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.utils.CustomDateSerializer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpcomingProductResult {
    private Long idx;
    private String name; // 상품이름
    private String posterUrl; // 포스터 이미지 경로 (파일명)

    @JsonSerialize(using = CustomDateSerializer.class)
    private LocalDateTime openDate;

    public static UpcomingProductResult from(Product product) {
        return UpcomingProductResult.builder()
                .idx(product.getIdx())
                .name(product.getName())
                .posterUrl(product.getProductImage().getFileName())
                .openDate(product.getOpenDate())
                .build();
    }
}
