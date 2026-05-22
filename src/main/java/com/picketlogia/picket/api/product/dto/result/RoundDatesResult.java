package com.picketlogia.picket.api.product.dto.result;

import com.picketlogia.picket.api.product.model.RoundDate;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoundDatesResult {
    private List<RoundDateResult> dates;

    public static RoundDatesResult from(List<RoundDate> entities) {
        return RoundDatesResult.builder().dates(entities.stream().map(RoundDateResult::from).toList()).build();
    }
}
