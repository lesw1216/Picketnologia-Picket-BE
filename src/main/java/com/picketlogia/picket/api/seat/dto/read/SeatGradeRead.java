package com.picketlogia.picket.api.seat.dto.read;

import com.picketlogia.picket.api.seat.model.SeatGrade;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatGradeRead {

    private SeatGradeStatus grade;
    private MoneyFormat priceInfo;

    public static SeatGradeRead from(SeatGrade entity) {
        return SeatGradeRead.builder()
                .grade(entity.getGrade())
                .priceInfo(
                        MoneyFormat.from(entity.getPrice())
                )
                .build();
    }
}
