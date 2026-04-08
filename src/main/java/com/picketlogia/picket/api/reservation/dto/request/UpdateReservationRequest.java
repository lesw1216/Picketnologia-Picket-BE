package com.picketlogia.picket.api.reservation.dto.request;

import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateReservationRequest {

    private Long price;
    private LocalDateTime paidAt;
    private PaymentStatus paymentStatus;

    public static UpdateReservationRequest from(Long price, LocalDateTime paidAt, Long productIdx, PaymentStatus paymentStatus) {
        return UpdateReservationRequest.builder()
                .price(price)
                .paidAt(paidAt)
                .paymentStatus(paymentStatus)
                .build();
    }
}
