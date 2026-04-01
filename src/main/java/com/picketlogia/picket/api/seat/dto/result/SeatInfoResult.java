package com.picketlogia.picket.api.seat.dto.result;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SeatInfoResult {

    private List<SeatGradeResult> seatGrades;
    private List<List<SeatResult>> seatMap;

    public static SeatInfoResult from(List<SeatGradeResult> seatGrades, List<List<SeatResult>> seatMap) {
        return SeatInfoResult.builder()
                .seatGrades(seatGrades)
                .seatMap(seatMap)
                .build();
    }
}
