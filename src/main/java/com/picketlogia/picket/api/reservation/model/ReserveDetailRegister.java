package com.picketlogia.picket.api.reservation.model;

import com.picketlogia.picket.api.payments.dto.command.PaymentCustomDataCommand;
import com.picketlogia.picket.api.product.model.entity.RoundTime;
import com.picketlogia.picket.api.reservation.model.entity.Reservation;
import com.picketlogia.picket.api.reservation.model.entity.ReserveDetail;
import com.picketlogia.picket.api.seat.model.Seat;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReserveDetailRegister {
    private Long reservationIdx;
    private Long roundTimeIdx;
    private List<Long> seatIdes;

    public static ReserveDetailRegister from(PaymentCustomDataCommand paymentData, Long reservationIdx) {
        return ReserveDetailRegister.builder()
                .reservationIdx(reservationIdx)
                .roundTimeIdx(paymentData.getRoundTimeIdx())
                .seatIdes(paymentData.getSeatIdxes())
                .build();
    }

    public List<ReserveDetail> toEntities() {
        return seatIdes.stream().map(
                seatId -> ReserveDetail.builder()
                        .seat(Seat.builder().idx(seatId).build())
                        .reservation(
                                Reservation.builder().idx(reservationIdx).build()
                        )
                        .roundTime(RoundTime.builder().idx(roundTimeIdx).build())
                        .build()
        ).toList();
    }
}
