package com.picketlogia.picket.api.seat.dto.response;

import com.picketlogia.picket.api.seat.dto.result.SeatPaymentResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatPaymentResponse {
    private Long price;

    public static SeatPaymentResponse from(SeatPaymentResult result) {
        return SeatPaymentResponse.builder()
                .price(result.getPrice())
                .build();
    }
}
