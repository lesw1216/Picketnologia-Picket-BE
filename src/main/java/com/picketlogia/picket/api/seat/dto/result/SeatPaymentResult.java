package com.picketlogia.picket.api.seat.dto.result;

import com.picketlogia.picket.api.seat.model.Seat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatPaymentResult {
    private Long price;

    public static SeatPaymentResult from(Seat entity) {
        return SeatPaymentResult.builder()
                .price(entity.getSeatGrade().getPrice())
                .build();
    }
}
