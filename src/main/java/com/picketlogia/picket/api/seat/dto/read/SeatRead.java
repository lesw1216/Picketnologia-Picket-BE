package com.picketlogia.picket.api.seat.dto.read;

import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatRead {

    private Long idx;
    private String name;
    private SeatGradeStatus grade;
    private MoneyFormat priceInfo;
    private Boolean isReserved;

    public static SeatRead from(Seat entity, Boolean isReserved) {
        return SeatRead.builder()
                .idx(entity.getIdx())
                .name(entity.getName())
                .grade(entity.getSeatGrade().getGrade())
                .priceInfo(
                        MoneyFormat.from(entity.getSeatGrade().getPrice())
                )
                .isReserved(isReserved)
                .build();
    }
}
