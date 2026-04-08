package com.picketlogia.picket.api.reservation.model;

import com.picketlogia.picket.api.payments.dto.command.PaymentCustomDataCommand;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReservationCheck {
    private Long roundTimeIdx;
    private Long productIdx;
    private List<Long> seatIdxes;

    public static ReservationCheck from(PaymentCustomDataCommand customData) {
        return ReservationCheck.builder()
                .roundTimeIdx(customData.getRoundTimeIdx())
                .seatIdxes(customData.getSeatIdxes())
                .build();
    }
}