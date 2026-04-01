package com.picketlogia.picket.api.seat.dto.response;

import com.picketlogia.picket.api.seat.dto.result.SeatGradeResult;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatGradeResponse {

    private SeatGradeStatus grade;
    private PriceResponse priceInfo;

    public static SeatGradeResponse from(SeatGradeResult result) {
        return SeatGradeResponse.builder()
                .grade(result.getGrade())
                .priceInfo(PriceResponse.from(result.getPriceInfo()))
                .build();
    }
}
