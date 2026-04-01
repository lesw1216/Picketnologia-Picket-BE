package com.picketlogia.picket.api.seat.dto.command;

import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.seat.dto.request.SeatRequest;
import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.model.SeatGrade;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SeatSaveCommand {

    private String name;
    private SeatGradeStatus grade;

    public Seat toEntity(Long gradeIdx, Long productIdx) {
        return Seat.builder()
                .name(name)
                .seatGrade(
                        SeatGrade.builder()
                                .idx(gradeIdx)
                                .build()
                )
                .product(
                        Product.builder()
                                .idx(productIdx)
                                .build()
                )
                .build();
    }

    public static List<List<SeatSaveCommand>> fromSeatMap(List<List<SeatRequest>> seatRequests) {
        return seatRequests.stream()
                .map(row -> row.stream()
                        .map(request -> SeatSaveCommand.builder()
                                .name(request.getName())
                                .grade(request.getGrade())
                                .build())
                        .toList())
                .toList();
    }
}
