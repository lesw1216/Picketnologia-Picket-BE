package com.picketlogia.picket.api.product.dto;

import com.picketlogia.picket.api.product.model.entity.RoundDate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RoundDateRead {
    private Long idx;
    private LocalDate date;

    public static RoundDateRead from(RoundDate entity) {
        return RoundDateRead.builder()
                .idx(entity.getIdx())
                .date(entity.getDate())
                .build();
    }
}
