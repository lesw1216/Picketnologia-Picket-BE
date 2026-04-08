package com.picketlogia.picket.api.payments.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picketlogia.picket.api.payments.dto.command.PaymentCustomDataCommand;
import com.picketlogia.picket.api.payments.dto.command.PaymentCommand;
import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import com.picketlogia.picket.api.reservation.model.ReserveDetailRegister;
import com.picketlogia.picket.api.reservation.model.UpdateReservationReq;
import com.picketlogia.picket.api.reservation.service.ReservationService;
import com.picketlogia.picket.api.reservation.service.ReserveDetailService;
import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.repository.SeatRepository;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import io.portone.sdk.server.PortOneClient;
import io.portone.sdk.server.payment.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class PaymentService {

    private final ReserveDetailService reserveDetailService;
    private final PortOneClient portOneClient;
    private final SeatRepository seatRepository;

    private final ReservationService reservationService;

    public PaymentService(SeatRepository seatRepository,
                          ReservationService reservationService,
                          ReserveDetailService reserveDetailService,
                          @Value("${portone.key.api-secret}") String apiSecret,
                          @Value("${portone.key.store-id}") String storeId
                          ) {

        this.portOneClient = new PortOneClient(
                apiSecret,
                "https://api.portone.io",
                storeId
        );

        this.seatRepository = seatRepository;
        this.reservationService = reservationService;
        this.reserveDetailService = reserveDetailService;
    }

    /**
     * 클라이언트가 요청으로 준 결제 완료 정보를 검증하여 결제 처리를 완료한다.
     * @param paymentId 결제 ID
     * @return Payment
     */
    @Transactional
    public Payment completePayment(String paymentId) {

        PaymentClient paymentClient = portOneClient.getPayment();
        CompletableFuture<Payment> completableFuture = paymentClient.getPayment(paymentId);

        Payment payment = completableFuture.join();

        // 성공 처리
        if (payment instanceof PaidPayment paidPayment) {
            if (verifyPayment(paidPayment)) {
                log.info(paidPayment.getPgResponse());
                PaymentCustomDataCommand customData = parseToPaymentCustomData(paidPayment.getCustomData());

//                결제 정보를 예매 정보로 저장
                requestReservation(paymentId, paidPayment, customData);
                return null;
            }

            throw BaseException.from(BaseResponseStatus.ERROR_PAYMENT_STATUS_PAID);
        }

        // 실패 처리
        if (payment instanceof FailedPayment failedPayment) {
            log.error(failedPayment.getFailure().toString());
        }

        // 취소 처리
        if (payment instanceof CancelledPayment cancelledPayment) {
            log.info(cancelledPayment.getCancellations().toString());
        }

        return payment;
    }

    /**
     * 결제 성공 시 예매 저장 요청
     * @param paymentId 결제 ID
     * @param paidPayment 결제 정보
     * @param customData 사용자 지정 데이터
     */
    private void requestReservation(String paymentId, PaidPayment paidPayment, PaymentCustomDataCommand customData) {
        UpdateReservationReq update = UpdateReservationReq.from(
                paidPayment.getAmount().getTotal(),
                getPaidAt(paidPayment),
                customData.getProductIdx(),
                PaymentStatus.PAID);

        Long reservationId = reservationService.updateReservation(update, paymentId);

        reserveDetailService.register(
                ReserveDetailRegister.from(customData, reservationId)
        );
    }

    /**
     * 수동 승인 결제 건 처리하기 (개발 중)
     */
    private void manualApprove(ReadyPayment readyPayment, PaymentCommand paymentCommand)
            throws ExecutionException, InterruptedException {

        Map<String, Object> request = new HashMap<>();
        request.put("totalAmount", readyPayment.getAmount().getTotal());
//        request.put("paymentToken", paymentReq.getPaymentToken());
//        request.put("txId", paymentReq.getTxId());
        ObjectMapper mapper = new ObjectMapper();

        String requestBody = "";
        try {
            requestBody = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String s = HttpClient.newHttpClient().sendAsync(
                HttpRequest.newBuilder()
                        .header("Authorization", "PortOne 시크릿키")
                        .header("Content-Type", "application/json")
                        .uri(URI.create("https://api.portone.io/payments/" + paymentCommand.getPaymentId() + "/confirm"))
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build(), HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).get();

        log.info("승인 요청 결과 = {}", s);
    }

    /**
     * 결제 정보에서 결제일을 추출한다.
     *
     * @param paidPayment 결제 정보
     * @return 결제일
     */
    private LocalDateTime getPaidAt(PaidPayment paidPayment) {
        return LocalDateTime.ofInstant(paidPayment.getPaidAt(), ZoneId.of("Asia/Seoul"));
    }

    /**
     * 결제 정보를 검증한다.
     *
     * @param paidPayment 결제 정보
     * @return 검증 성공은 <code>true</code> 실패는 <code>false</code>
     */
    private boolean verifyPayment(PaidPayment paidPayment) {
//        if (!paidPayment.getChannel().getType().equals(SelectedChannelType.Live.INSTANCE))
//            return false;

        PaymentCustomDataCommand paymentCustomDataCommand = parseToPaymentCustomData(paidPayment.getCustomData());

        if (paymentCustomDataCommand != null) {
            return verifyPaymentAmount(paymentCustomDataCommand, paidPayment.getAmount());
        }

        return false;
    }

    /**
     * 결제시 담은 사용자 지정 데이터
     *
     * @param customData 사용자 지정 정보
     * @return 사용자 지정 데이터
     */
    private PaymentCustomDataCommand parseToPaymentCustomData(String customData) {
        return PaymentCustomDataCommand.from(customData);
    }

    /**
     * 결제 금액과 DB에서 조회한 결제 상품들의 총 합을 비교한다.
     *
     * @param paymentCustomDataCommand 결제한 상품 정보
     * @param paymentAmount     결제 금액
     * @return 값이 같다면 <code>true</code>, 다르다면 <code>false</code>
     */
    private Boolean verifyPaymentAmount(PaymentCustomDataCommand paymentCustomDataCommand, PaymentAmount paymentAmount) {

        // 구매한 좌석의 실제 총 금액을 알기 위해 좌석 정보를 조회
        List<Seat> getSeats = seatRepository.findByIdInWithSeatGrade(
                paymentCustomDataCommand.getSeatIdxes().stream().map(idx -> Seat.builder().idx(idx).build()).toList()
        );

        // 총 결제 금액과 조회한 좌석의 총 금액이 같은지 비교
        Long actualPrice = getSeats.stream().mapToLong(seat -> seat.getSeatGrade().getPrice()).sum();
        log.info("actualPrice = {} ", actualPrice);
        log.info("paymentAmount.getTotal() = {}", paymentAmount.getTotal());
        return actualPrice.equals(paymentAmount.getTotal());
    }
}
