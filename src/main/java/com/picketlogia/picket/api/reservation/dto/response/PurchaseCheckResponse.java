package com.picketlogia.picket.api.reservation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseCheckResponse {
        private Boolean hasPurchase;
}
