package com.picketlogia.picket.api.seat.dto.result;

import com.picketlogia.picket.api.seat.model.SeatGrade;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatGradeResult {

    private SeatGradeStatus grade;
    private PriceResult priceInfo;

    public static SeatGradeResult from(SeatGrade entity) {
        return SeatGradeResult.builder()
                .grade(entity.getGrade())
                .priceInfo(PriceResult.from(entity.getPrice()))
                .build();
    }
}
