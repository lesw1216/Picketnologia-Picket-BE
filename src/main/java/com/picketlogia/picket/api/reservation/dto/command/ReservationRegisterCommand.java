package com.picketlogia.picket.api.reservation.dto.command;

import com.picketlogia.picket.api.payments.dto.command.PaymentCustomDataCommand;
import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import com.picketlogia.picket.api.reservation.model.Reservation;
import com.picketlogia.picket.api.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationRegisterCommand {
    private String paymentIdx;
    private Long userIdx;
    private Long productIdx;
    private Long price;
    private LocalDateTime paidAt;
    private PaymentStatus paymentStatus;

    public static ReservationRegisterCommand from(Long userIdx,
                                                  String paymentIdx,
                                                  Long price,
                                                  LocalDateTime paidAt,
                                                  PaymentCustomDataCommand paymentData) {

        return ReservationRegisterCommand.builder()
                .paymentIdx(paymentIdx)
                .price(price)
                .userIdx(userIdx)
                .paidAt(paidAt)
                .productIdx(paymentData.getProductIdx())
                .build();

    }

    public static ReservationRegisterCommand from(Long userIdx, Long productIdx, String paymentIdx, PaymentStatus paymentStatus) {

        return ReservationRegisterCommand.builder()
                .userIdx(userIdx)
                .paymentIdx(paymentIdx)
                .paymentStatus(paymentStatus)
                .price(null)
                .productIdx(productIdx)
                .paidAt(null)
                .build();
    }

    public Reservation toEntity() {
        return Reservation.builder()
                .paymentIdx(this.paymentIdx)
                .paidAt(this.paidAt)
                .user(
                        User.builder().idx(this.userIdx).build()
                )
                .product(
                        this.productIdx != null ? Product.builder().idx(this.productIdx).build() : null
                )
                .price(this.price)
                .paymentStatus(this.paymentStatus)
                .build();
    }

}