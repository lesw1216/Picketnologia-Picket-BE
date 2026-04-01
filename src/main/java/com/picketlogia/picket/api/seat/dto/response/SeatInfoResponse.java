package com.picketlogia.picket.api.seat.dto.response;

import com.picketlogia.picket.api.seat.dto.result.SeatInfoResult;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SeatInfoResponse {

    private List<SeatGradeResponse> seatGrades;
    private List<List<SeatResponse>> seatMap;

    public static SeatInfoResponse from(SeatInfoResult result) {
        return SeatInfoResponse.builder()
                .seatGrades(result.getSeatGrades().stream().map(SeatGradeResponse::from).toList())
                .seatMap(
                        result.getSeatMap().stream()
                                .map(row -> row.stream().map(SeatResponse::from).toList())
                                .toList()
                )
                .build();
    }
}
