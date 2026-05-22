package com.picketlogia.picket.api.product.dto.result;

import com.picketlogia.picket.api.product.model.RoundTime;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoundTimesResult {

    private List<RoundTimeResult> times;

    public static RoundTimesResult from(List<RoundTime> entity) {

        return RoundTimesResult.builder()
                .times(entity.stream().map(RoundTimeResult::from).toList())
                .build();
    }
}
