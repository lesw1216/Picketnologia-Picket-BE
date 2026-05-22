package com.picketlogia.picket.api.review.model.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.picketlogia.picket.api.review.model.entity.Review;
import com.picketlogia.picket.utils.CustomDateSerializer;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class  ReviewDtoList {
    private Integer rating;

    private String comment;

    @JsonFormat(pattern = "MM.dd(E) HH:mm", timezone = "Asia/Seoul")
    private Date createdAt;

    @JsonFormat(pattern = "MM.dd(E) HH:mm", timezone = "Asia/Seoul")
    private Date updatedAt;

    private String prodcutName;

    private String userNickName;

    public static ReviewDtoList from(Review entity){

        ReviewDtoList dto = ReviewDtoList.builder()
                .prodcutName(entity.getProduct().getName())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .userNickName(entity.getUser().getName())
                .build();
        return dto;
    }
}
