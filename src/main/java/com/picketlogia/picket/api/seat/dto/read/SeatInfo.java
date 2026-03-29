package com.picketlogia.picket.api.seat.dto.read;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SeatInfo {

    private List<SeatGradeRead> seatGrades;
    private List<List<SeatRead>> seatMap;

    public static SeatInfo from(List<SeatGradeRead> seatGrades, List<List<SeatRead>> seatMap) {
        return SeatInfo.builder()
                .seatGrades(seatGrades)
                .seatMap(seatMap)
                .build();
    }
}
