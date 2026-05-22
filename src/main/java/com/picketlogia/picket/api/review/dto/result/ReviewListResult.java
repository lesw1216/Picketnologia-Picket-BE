package com.picketlogia.picket.api.review.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.picketlogia.picket.api.review.model.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ReviewListResult {

    private Integer rating;

    private String comment;

    @JsonFormat(pattern = "MM.dd(E) HH:mm", timezone = "Asia/Seoul")
    private Date createdAt;

    @JsonFormat(pattern = "MM.dd(E) HH:mm", timezone = "Asia/Seoul")
    private Date updatedAt;

    private String prodcutName;

    private String userNickName;

    public static ReviewListResult from(Review entity) {

        return ReviewListResult.builder()
                .prodcutName(entity.getProduct().getName())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .userNickName(entity.getUser().getName())
                .build();
    }
}
