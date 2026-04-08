package com.picketlogia.picket.api.product.dto;

import com.picketlogia.picket.api.product.model.entity.RoundDate;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoundDatesResult {
    private List<RoundDateRead> dates;

    public static RoundDatesResult from(List<RoundDate> entities) {
        return RoundDatesResult.builder().dates(entities.stream().map(RoundDateRead::from).toList()).build();
    }
}
