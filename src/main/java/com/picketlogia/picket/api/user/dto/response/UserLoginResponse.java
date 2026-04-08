package com.picketlogia.picket.api.user.dto.response;

import com.picketlogia.picket.api.user.dto.request.UserAuthRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponse {

    private Long idx;
    private String nickname;
    private String role;
    private String userType;

    public static UserLoginResponse from(UserAuthRequest authUser) {
        return UserLoginResponse.builder()
                .idx(authUser.getIdx())
                .nickname(authUser.getNickname())
                .role(authUser.getRole())
                .userType(authUser.getUserType())
                .build();
    }
}
