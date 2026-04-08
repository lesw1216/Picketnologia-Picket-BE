package com.picketlogia.picket.api.region.dto.result;

import com.picketlogia.picket.api.region.model.Region;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RegionResults {

    private List<RegionResult> regions;

    public static RegionResults of(List<Region> regions) {

        return RegionResults.builder()
                .regions(
                        regions.stream().map(RegionResult::of).toList()
                )
                .build();
    }
}
