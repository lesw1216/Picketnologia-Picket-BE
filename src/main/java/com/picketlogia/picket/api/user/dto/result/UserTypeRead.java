package com.picketlogia.picket.api.user.dto.result;

import com.picketlogia.picket.api.user.model.enums.UserType;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class UserTypeRead {

    private String code;
    private String name;

    private static UserTypeRead from(UserType userType) {
        return UserTypeRead.builder()
                .code(userType.name())
                .name(userType.getName())
                .build();
    }

    public static List<UserTypeRead> fromList() {

        List<UserType> userTypes = Arrays.stream(UserType.values()).toList();

        return userTypes.stream().map(UserTypeRead::from).toList();
    }
}
