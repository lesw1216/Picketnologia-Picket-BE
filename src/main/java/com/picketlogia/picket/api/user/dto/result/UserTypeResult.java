package com.picketlogia.picket.api.user.dto.result;

import com.picketlogia.picket.api.user.model.enums.UserType;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class UserTypeResult {

    private String code;
    private String name;

    private static UserTypeResult from(UserType userType) {
        return UserTypeResult.builder()
                .code(userType.name())
                .name(userType.getName())
                .build();
    }

    public static List<UserTypeResult> fromList() {

        List<UserType> userTypes = Arrays.stream(UserType.values()).toList();

        return userTypes.stream().map(UserTypeResult::from).toList();
    }
}
