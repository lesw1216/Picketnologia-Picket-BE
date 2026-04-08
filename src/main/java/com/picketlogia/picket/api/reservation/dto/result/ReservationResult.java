package com.picketlogia.picket.api.reservation.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.picketlogia.picket.api.reservation.model.Reservation;
import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
public class ReservationResult {

    private Long idx;
    private Long price;
    private String paymentIdx;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime paidAt;
    private PaymentStatus paymentStatus;

    private String productName;
    private String seat;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;

    public static ReservationResult from(Reservation entity) {

        return ReservationResult.builder()
                .idx(entity.getIdx())
                .price(entity.getPrice())
                .paymentIdx(entity.getPaymentIdx())
                .paidAt(entity.getPaidAt())
                .paymentStatus(entity.getPaymentStatus())
                .productName(entity.getProduct().getName())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
