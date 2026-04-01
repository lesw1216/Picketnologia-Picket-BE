package com.picketlogia.picket.api.seat.dto.result;

import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatResult {

    private Long idx;
    private String name;
    private SeatGradeStatus grade;
    private PriceResult priceInfo;
    private Boolean isReserved;

    public static SeatResult from(Seat entity, Boolean isReserved) {
        return SeatResult.builder()
                .idx(entity.getIdx())
                .name(entity.getName())
                .grade(entity.getSeatGrade().getGrade())
                .priceInfo(PriceResult.from(entity.getSeatGrade().getPrice()))
                .isReserved(isReserved)
                .build();
    }
}
