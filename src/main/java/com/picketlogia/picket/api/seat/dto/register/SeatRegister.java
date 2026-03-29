package com.picketlogia.picket.api.seat.dto.register;

import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.model.SeatGrade;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatRegister {

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
}
