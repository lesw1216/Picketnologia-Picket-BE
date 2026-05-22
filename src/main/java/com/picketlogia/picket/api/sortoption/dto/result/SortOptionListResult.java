package com.picketlogia.picket.api.sortoption.dto.result;

import com.picketlogia.picket.api.sortoption.model.SortOption;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SortOptionListResult {
    private List<SortOptionResult> sortOptions;

    public static SortOptionListResult from(List<SortOption> sortOptions) {
        return SortOptionListResult.builder()
                .sortOptions(
                        sortOptions.stream().map(SortOptionResult::from).toList()
                )
                .build();
    }
}
