package com.picketlogia.picket.api.user.dto.response;

import com.picketlogia.picket.api.user.dto.result.GenderResult;
import com.picketlogia.picket.api.user.dto.result.UserTypeResult;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SignupResponse {

    private List<GenderResult> genders;
    private List<UserTypeResult> userTypes;

    public static SignupResponse from() {
        return SignupResponse.builder()
                .genders(GenderResult.fromList())
                .userTypes(UserTypeResult.fromList())
                .build();
    }
}
