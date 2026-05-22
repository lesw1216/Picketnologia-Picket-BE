package com.picketlogia.picket.api.sortoption.dto.result;

import com.picketlogia.picket.api.sortoption.model.SortOption;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SortOptionResult {
    private String code;
    private String name;

    public static SortOptionResult from(SortOption sortOption) {
        return SortOptionResult.builder()
                .code(sortOption.name())
                .name(sortOption.getName())
                .build();
    }
}
