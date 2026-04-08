package com.picketlogia.picket.api.reservation.dto.request;

import com.picketlogia.picket.api.payments.dto.command.PaymentCustomDataCommand;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReservationCheckRequest {
    private Long roundTimeIdx;
    private Long productIdx;
    private List<Long> seatIdxes;

    public static ReservationCheckRequest from(PaymentCustomDataCommand customData) {
        return ReservationCheckRequest.builder()
                .roundTimeIdx(customData.getRoundTimeIdx())
                .seatIdxes(customData.getSeatIdxes())
                .build();
    }
}