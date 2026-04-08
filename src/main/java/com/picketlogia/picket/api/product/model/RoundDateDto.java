package com.picketlogia.picket.api.product.model;

import com.picketlogia.picket.api.product.model.entity.RoundDate;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundDateDto {

    private LocalDate date;
    private List<RoundTimeDto> roundTimes;

    // Entity -> DTO
    public static RoundDateDto from(RoundDate entity) {

        return RoundDateDto.builder()
                .date(entity.getDate())
                .roundTimes(entity.getRoundTimes().stream().map(RoundTimeDto::from).collect(Collectors.toList()))
                .build();
    }
}
