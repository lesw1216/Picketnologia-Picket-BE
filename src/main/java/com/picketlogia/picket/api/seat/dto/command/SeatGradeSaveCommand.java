package com.picketlogia.picket.api.seat.dto.command;

import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.seat.dto.request.SeatGradeRequest;
import com.picketlogia.picket.api.seat.model.SeatGrade;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SeatGradeSaveCommand {

    private SeatGradeStatus code;
    private Long price;

    public SeatGrade toEntity(Long productIdx) {
        return SeatGrade.builder()
                .grade(code)
                .price(price)
                .product(
                        Product.builder()
                                .idx(productIdx)
                                .build()
                )
                .build();
    }

    public static List<SeatGradeSaveCommand> fromList(List<SeatGradeRequest> seatGradeRequests) {
        return seatGradeRequests.stream()
                .map(request -> SeatGradeSaveCommand.builder()
                        .code(request.getCode())
                        .price(request.getPrice())
                        .build())
                .toList();
    }
}
