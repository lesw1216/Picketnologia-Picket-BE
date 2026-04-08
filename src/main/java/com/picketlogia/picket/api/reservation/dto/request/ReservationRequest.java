package com.picketlogia.picket.api.reservation.dto.request;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.reservation.model.Reservation;
import com.picketlogia.picket.api.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReservationRequest {
    private Long price;
    private String paymentsIdx;
    private Long userIdx;
    private Long productIdx;
    private List<Long> seatIdxList;

    public static ReservationRequest from(Long userIdx, Long productIdx, List<Long> seatIdxList) {
        return ReservationRequest.builder()
                .userIdx(userIdx)
                .productIdx(productIdx)
                .seatIdxList(seatIdxList)
                .build();
    }

    public Reservation toReserveEntity(Reservation entity) {

        return Reservation.builder()
                .price(entity.getPrice())
                .paymentIdx(entity.getPaymentIdx())
                .user(
                        User.builder().idx(entity.getUser().getIdx()).build()
                )
                .product(
                        Product.builder().idx(entity.getProduct().getIdx()).build()
                )
                .build();
    }
}
