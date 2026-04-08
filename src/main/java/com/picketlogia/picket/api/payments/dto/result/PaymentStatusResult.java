package com.picketlogia.picket.api.payments.dto.result;

import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentStatusResult {

    private PaymentStatus status;

    public static PaymentStatusResult from(PaymentStatus status) {
        return PaymentStatusResult.builder()
                .status(status)
                .build();
    }
}
