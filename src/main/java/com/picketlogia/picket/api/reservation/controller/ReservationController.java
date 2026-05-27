package com.picketlogia.picket.api.reservation.controller;

import com.picketlogia.picket.api.payments.dto.response.PaymentPrepareResponse;
import com.picketlogia.picket.api.payments.util.PaymentIdGenerator;
import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import com.picketlogia.picket.api.reservation.dto.response.PurchaseCheckResponse;
import com.picketlogia.picket.api.reservation.dto.request.ReservationCheckRequest;
import com.picketlogia.picket.api.reservation.dto.command.ReservationRegisterCommand;
import com.picketlogia.picket.api.reservation.dto.result.ReservationResult;
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

    /**
     * 로그인 사용자가 특정 상품을 구매한 적이 있는지 확인한다.
     *
     * @param loginUser  요청자 인증 정보
     * @param productIdx 확인할 상품 ID
     * @return 구매 여부 결과를 담은 표준 응답
     */
    @GetMapping("/check")
    public ResponseEntity<BaseResponse<PurchaseCheckResponse>> hasPurchased(@AuthenticationPrincipal UserAuthRequest loginUser,
                                                                            @RequestParam Long productIdx) {

        Long userIdx = loginUser.getIdx();

        Boolean hasPurchased = reservationService.hasPurchasedProduct(userIdx, productIdx);

        PurchaseCheckResponse purchaseCheckResponse = PurchaseCheckResponse.builder().hasPurchase(hasPurchased).build();

        return ResponseEntity.ok(BaseResponse.success(purchaseCheckResponse));
    }

    /**
     * 결제 직전 좌석 중복·잠금 유효성을 검증한 뒤 결제 ID를 발급해 예매를 PENDING 으로 저장한다.
     *
     * @param userAuth         요청자 인증 정보
     * @param reservationCheck 좌석·회차가 담긴 예매 검증 요청
     * @return 발급된 결제 ID를 담은 표준 응답
     */
    @PostMapping("/validate-seats")
    public ResponseEntity<BaseResponse<PaymentPrepareResponse>> validateReservation(@AuthenticationPrincipal UserAuthRequest userAuth,
                                                                                    @RequestBody ReservationCheckRequest reservationCheck) {

        reservationService.validateSeatsNotReserved(reservationCheck);
        reservationService.validateLockedSeats(reservationCheck);

        String paymentIdx = PaymentIdGenerator.generatePaymentId();
        reservationService.register(
                ReservationRegisterCommand.from(
                        userAuth.getIdx(),
                        reservationCheck.getProductIdx(),
                        paymentIdx,
                        PaymentStatus.PENDING)
        );

        return ResponseEntity.ok(BaseResponse.success(
                PaymentPrepareResponse.builder().paymentIdx(paymentIdx).build())
        );
    }


    /**
     * 사용자가 지정 기간 동안 진행한 예매 내역을 조회한다.
     *
     * @param userAuth  요청자 인증 정보
     * @param startDate 시작일 (yyyy-MM-dd)
     * @param endDate   종료일 (yyyy-MM-dd)
     * @return 예매 내역 목록을 담은 표준 응답
     */
    @GetMapping("/ReservationList")
    public ResponseEntity<BaseResponse<List<ReservationResult>>> getUserReviewsByDate(@AuthenticationPrincipal UserAuthRequest userAuth,
                                                                                      @RequestParam("startDate") String startDate,
                                                                                      @RequestParam("endDate") String endDate) {

        List<ReservationResult> response = reservationService.findAllByUserIdxAndCreatedAtBetween(userAuth.getIdx(), startDate, endDate);

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
