package com.picketlogia.picket.api.payments.model;

import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentStatusResponse {

    private PaymentStatus status;

    public static PaymentStatusResponse from(PaymentStatus status) {
        return PaymentStatusResponse.builder()
                .status(status)
                .build();
    }
}
