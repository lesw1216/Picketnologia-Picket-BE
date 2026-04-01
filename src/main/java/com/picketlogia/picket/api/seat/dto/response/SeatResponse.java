package com.picketlogia.picket.api.seat.dto.response;

import com.picketlogia.picket.api.seat.dto.result.SeatResult;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatResponse {

    private Long idx;
    private String name;
    private SeatGradeStatus grade;
    private PriceResponse priceInfo;
    private Boolean isReserved;

    public static SeatResponse from(SeatResult result) {
        return SeatResponse.builder()
                .idx(result.getIdx())
                .name(result.getName())
                .grade(result.getGrade())
                .priceInfo(PriceResponse.from(result.getPriceInfo()))
                .isReserved(result.getIsReserved())
                .build();
    }
}
