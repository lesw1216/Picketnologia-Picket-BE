package com.picketlogia.picket.api.region.dto.result;

import com.picketlogia.picket.api.region.model.Region;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionResult {
    private String code;
    private String name;

    public static RegionResult of(Region region) {

        return RegionResult.builder()
                .code(region.getCode())
                .name(region.getName())
                .build();
    }
}
