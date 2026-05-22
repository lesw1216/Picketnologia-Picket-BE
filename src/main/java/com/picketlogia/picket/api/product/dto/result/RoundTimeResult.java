package com.picketlogia.picket.api.product.dto.result;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.picketlogia.picket.api.product.model.RoundTime;
import com.picketlogia.picket.utils.RoundTimeSerializer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class RoundTimeResult {

    private Long idx;

    @JsonSerialize(using = RoundTimeSerializer.class)
    private LocalTime time;

    public static RoundTimeResult from(RoundTime entity) {
        return RoundTimeResult.builder()
                .idx(entity.getIdx())
                .time(entity.getTime())
                .build();
    }
}
