package com.picketlogia.picket.api.reservation.dto.command;

import com.picketlogia.picket.api.payments.dto.command.PaymentCustomDataCommand;
import com.picketlogia.picket.api.product.model.RoundTime;
import com.picketlogia.picket.api.reservation.model.Reservation;
import com.picketlogia.picket.api.reservation.model.ReserveDetail;
import com.picketlogia.picket.api.seat.model.Seat;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReserveDetailRegisterCommand {
    private Long reservationIdx;
    private Long roundTimeIdx;
    private List<Long> seatIdes;

    public static ReserveDetailRegisterCommand from(PaymentCustomDataCommand paymentData, Long reservationIdx) {
        return ReserveDetailRegisterCommand.builder()
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
