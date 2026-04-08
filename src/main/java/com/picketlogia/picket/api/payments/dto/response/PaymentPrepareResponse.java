package com.picketlogia.picket.api.payments.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentPrepareResponse {

    @NotBlank
    private String paymentIdx;
}
