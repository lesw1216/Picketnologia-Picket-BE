package com.picketlogia.picket.api.product.dto.result;

import com.picketlogia.picket.api.product.model.RoundDate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RoundDateResult {
    private Long idx;
    private LocalDate date;

    public static RoundDateResult from(RoundDate entity) {
        return RoundDateResult.builder()
                .idx(entity.getIdx())
                .date(entity.getDate())
                .build();
    }
}
