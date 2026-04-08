package com.picketlogia.picket.api.reservation.model;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.reservation.model.entity.Reservation;
import com.picketlogia.picket.api.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReserveReq {
    private Long price;
    private String paymentsIdx;
    private Long userIdx;
    private Long productIdx;
    private List<Long> seatIdxList;

    public static ReserveReq from(Long userIdx, Long productIdx, List<Long> seatIdxList) {
        return ReserveReq.builder()
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
