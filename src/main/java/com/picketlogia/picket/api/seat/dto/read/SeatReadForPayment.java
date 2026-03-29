package com.picketlogia.picket.api.seat.dto.read;

import com.picketlogia.picket.api.seat.model.Seat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatReadForPayment {
    private Long price;

    public static SeatReadForPayment from(Seat entity) {
        return SeatReadForPayment.builder()
                .price(entity.getSeatGrade().getPrice())
                .build();
    }
}