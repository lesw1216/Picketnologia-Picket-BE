package com.picketlogia.picket.api.product.model;

import com.picketlogia.picket.api.product.model.entity.RoundTime;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundTimeDto {

    private LocalTime times;

    // Entity -> DTO
    public static RoundTimeDto from(RoundTime entity) {

        return RoundTimeDto.builder()
                .times(entity.getTime())
                .build();
    }
}
