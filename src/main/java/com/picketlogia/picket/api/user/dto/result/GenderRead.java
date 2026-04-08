package com.picketlogia.picket.api.user.dto.result;

import com.picketlogia.picket.api.user.model.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class GenderRead {

    private String code;
    private String name;

    private static GenderRead from(Gender gender) {
        return GenderRead.builder()
                .code(gender.name())
                .name(gender.getName())
                .build();
    }

    public static List<GenderRead> fromList() {

        List<Gender> genders = Arrays.stream(Gender.values()).toList();

        return genders.stream().map(GenderRead::from).toList();
    }
}