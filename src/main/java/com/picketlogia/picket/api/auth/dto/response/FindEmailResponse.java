package com.picketlogia.picket.api.auth.dto.response;

import com.picketlogia.picket.api.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindEmailResponse {
    private String email;

    public static FindEmailResponse from(User user) {
        return FindEmailResponse.builder().
                email(user.getEmail())
                .build();
    }
}
