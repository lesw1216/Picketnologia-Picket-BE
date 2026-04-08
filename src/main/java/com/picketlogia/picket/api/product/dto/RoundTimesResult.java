package com.picketlogia.picket.api.product.dto;

import com.picketlogia.picket.api.product.model.entity.RoundTime;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoundTimesResult {

    private List<RoundTimeRead> times;

    public static RoundTimesResult from(List<RoundTime> entity) {

        return RoundTimesResult.builder()
                .times(entity.stream().map(RoundTimeRead::from).toList())
                .build();
    }
}
