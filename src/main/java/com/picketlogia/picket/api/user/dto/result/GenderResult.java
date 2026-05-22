package com.picketlogia.picket.api.user.dto.result;

import com.picketlogia.picket.api.user.model.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class GenderResult {

    private String code;
    private String name;

    private static GenderResult from(Gender gender) {
        return GenderResult.builder()
                .code(gender.name())
                .name(gender.getName())
                .build();
    }

    public static List<GenderResult> fromList() {

        List<Gender> genders = Arrays.stream(Gender.values()).toList();

        return genders.stream().map(GenderResult::from).toList();
    }
}
