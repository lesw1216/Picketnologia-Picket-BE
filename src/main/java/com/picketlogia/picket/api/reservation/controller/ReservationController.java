package com.picketlogia.picket.api.reservation.controller;

import com.picketlogia.picket.api.payments.dto.response.PaymentPrepareResponse;
import com.picketlogia.picket.api.payments.util.PaymentIdGenerator;
import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import com.picketlogia.picket.api.reservation.model.PurchaseCheckResp;
import com.picketlogia.picket.api.reservation.model.ReservationCheck;
import com.picketlogia.picket.api.reservation.model.ReservationRegister;
import com.picketlogia.picket.api.reservation.model.dto.ReservationListDto;
import com.picketlogia.picket.api.reservation.repository.ReservationRepository;
import com.picketlogia.picket.api.reservation.service.ReservationService;
import com.picketlogia.picket.api.user.dto.request.UserAuthRequest;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    @GetMapping("/check")
    public ResponseEntity<BaseResponse<PurchaseCheckResp>> hasPurchased(
            @AuthenticationPrincipal UserAuthRequest loginUser,
            @RequestParam Long productIdx) {

        Long userIdx = loginUser.getIdx();

        Boolean hasPurchased = reservationService.hasPurchasedProduct(userIdx, productIdx);

        PurchaseCheckResp resp = PurchaseCheckResp.builder()
                .hasPurchase(hasPurchased)
                .build();
        return ResponseEntity.ok(BaseResponse.success(resp));
    }

    @PostMapping("/validate-seats")
    public ResponseEntity<BaseResponse<PaymentPrepareResponse>> checkReservedSeats(
            @AuthenticationPrincipal UserAuthRequest userAuth,
            @RequestBody ReservationCheck reservationCheck) {

        reservationService.checkReservedSeat(reservationCheck);
        reservationService.checkRockSeats(reservationCheck);

        // 좌석 검증 성공, 결제 ID 생성 후 결제 ID와 유저 ID DB에 저장 후 결제 상태를 PENDING 설정
        String paymentIdx = PaymentIdGenerator.generatePaymentId();
        reservationService.register(
                ReservationRegister.from(
                        userAuth.getIdx(),
                        reservationCheck.getProductIdx(),
                        paymentIdx,
                        PaymentStatus.PENDING)
        );

        return ResponseEntity.ok(BaseResponse.success(
                PaymentPrepareResponse.builder().paymentIdx(paymentIdx).build())
        );
    }


    @GetMapping("/ReservationList")
    public ResponseEntity<BaseResponse<List<ReservationListDto>>> getUserReviewsByDate(
            @AuthenticationPrincipal UserAuthRequest userAuth,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ) {
        List<ReservationListDto> response = reservationService.listByUserAndDateRange(userAuth.getIdx(), startDate, endDate);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
