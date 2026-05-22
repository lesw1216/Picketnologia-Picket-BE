package com.picketlogia.picket.api.payments.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCommand {
    private String paymentId;
}
