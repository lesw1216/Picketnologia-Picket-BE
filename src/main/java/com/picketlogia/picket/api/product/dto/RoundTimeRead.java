package com.picketlogia.picket.api.product.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.picketlogia.picket.api.product.model.RoundTime;
import com.picketlogia.picket.utils.RoundTimeSerializer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class RoundTimeRead {

    private Long idx;

    @JsonSerialize(using = RoundTimeSerializer.class)
    private LocalTime time;

    public static RoundTimeRead from(RoundTime entity) {
        return RoundTimeRead.builder()
                .idx(entity.getIdx())
                .time(entity.getTime())
                .build();
    }
}
