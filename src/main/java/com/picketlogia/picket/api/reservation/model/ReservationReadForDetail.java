package com.picketlogia.picket.api.reservation.model;

import com.picketlogia.picket.api.reservation.model.entity.Reservation;
import com.picketlogia.picket.api.reservation.model.entity.ReserveDetail;
import com.picketlogia.picket.api.seat.dto.read.MoneyFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReservationReadForDetail {

    private Long idx;
    private MoneyFormat totalPrice;
    private String paymentIdx;
    private String productName;
    private LocalDateTime paidAt;
    private List<ReserveDetailRead> details;

    private static ReservationReadForDetailBuilder baseBuilder(Reservation entity) {
        return ReservationReadForDetail.builder()
                .idx(entity.getIdx())
                .totalPrice(
                        MoneyFormat.from(entity.getPrice())
                )
                .paymentIdx(entity.getPaymentIdx())
                .paidAt(entity.getPaidAt())
                .productName(entity.getProduct().getName());
    }

    public static ReservationReadForDetail from(Reservation entity) {
        return ReservationReadForDetail.from(entity, entity.getReserveDetails());
    }

    public static ReservationReadForDetail from(Reservation reservation, List<ReserveDetail> details) {
        return baseBuilder(reservation)
                .details(
                        details.stream().map(ReserveDetailRead::from).toList()
                ).build();
    }
}
