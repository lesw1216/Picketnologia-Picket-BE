package com.picketlogia.picket.api.user.dto.response;

import com.picketlogia.picket.api.user.dto.result.GenderRead;
import com.picketlogia.picket.api.user.dto.result.UserTypeRead;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SignupResponse {

    private List<GenderRead> genders;
    private List<UserTypeRead> userTypes;

    public static SignupResponse from() {
        return SignupResponse.builder()
                .genders(GenderRead.fromList())
                .userTypes(UserTypeRead.fromList())
                .build();
    }
}
