package com.picketlogia.picket.api.payments.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picketlogia.picket.api.payments.dto.command.PaymentCustomDataCommand;
import com.picketlogia.picket.api.payments.dto.command.PaymentCommand;
import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import com.picketlogia.picket.api.reservation.dto.command.ReserveDetailRegisterCommand;
import com.picketlogia.picket.api.reservation.dto.request.UpdateReservationRequest;
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
                          @Value("${portone.key.store-id}") String storeId) {

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
     * 결제 ID로 PortOne 결제 정보를 조회·검증한 뒤 상태별로 처리한다.
     *
     * @param paymentId 결제 ID
     * @return 조회된 Payment (성공 후 추가 처리 시 null 반환)
     * @throws BaseException 결제 검증 실패 시
     */
    @Transactional
    public Payment completePayment(String paymentId) {

        Payment payment = fetchPayment(paymentId);

        if (payment instanceof PaidPayment paidPayment) {
            return handlePaidPayment(paymentId, paidPayment);
        }
        if (payment instanceof FailedPayment failedPayment) {
            log.error(failedPayment.getFailure().toString());
        }
        if (payment instanceof CancelledPayment cancelledPayment) {
            log.info(cancelledPayment.getCancellations().toString());
        }

        return payment;
    }

    private Payment fetchPayment(String paymentId) {

        PaymentClient paymentClient = portOneClient.getPayment();
        CompletableFuture<Payment> completableFuture = paymentClient.getPayment(paymentId);

        return completableFuture.join();
    }

    private Payment handlePaidPayment(String paymentId, PaidPayment paidPayment) {

        if (!verifyPayment(paidPayment)) {
            throw BaseException.from(BaseResponseStatus.ERROR_PAYMENT_STATUS_PAID);
        }

        log.info(paidPayment.getPgResponse());
        PaymentCustomDataCommand customData = parseToPaymentCustomData(paidPayment.getCustomData());

        requestReservation(paymentId, paidPayment, customData);

        return null;
    }

    private void requestReservation(String paymentId, PaidPayment paidPayment, PaymentCustomDataCommand customData) {

        UpdateReservationRequest update = UpdateReservationRequest.from(
                paidPayment.getAmount().getTotal(),
                getPaidAt(paidPayment),
                customData.getProductIdx(),
                PaymentStatus.PAID);

        Long reservationId = reservationService.updateReservation(update, paymentId);

        reserveDetailService.register(
                ReserveDetailRegisterCommand.from(customData, reservationId)
        );
    }

    private void manualApprove(ReadyPayment readyPayment, PaymentCommand paymentCommand)
            throws ExecutionException, InterruptedException {

        String requestBody = buildManualApproveBody(readyPayment);
        String response = sendManualApproveRequest(paymentCommand.getPaymentId(), requestBody);

        log.info("승인 요청 결과 = {}", response);
    }

    private String buildManualApproveBody(ReadyPayment readyPayment) {

        Map<String, Object> request = new HashMap<>();
        request.put("totalAmount", readyPayment.getAmount().getTotal());

        try {
            return new ObjectMapper().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("수동 승인 요청 body 직렬화 실패", e);
            throw BaseException.from(BaseResponseStatus.PAYMENT_DATA_INVALID);
        }
    }

    private String sendManualApproveRequest(String paymentId, String requestBody)
            throws ExecutionException, InterruptedException {

        return HttpClient.newHttpClient().sendAsync(
                HttpRequest.newBuilder()
                        .header("Authorization", "PortOne 시크릿키")
                        .header("Content-Type", "application/json")
                        .uri(URI.create("https://api.portone.io/payments/" + paymentId + "/confirm"))
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        ).thenApply(HttpResponse::body).get();
    }

    private LocalDateTime getPaidAt(PaidPayment paidPayment) {

        return LocalDateTime.ofInstant(paidPayment.getPaidAt(), ZoneId.of("Asia/Seoul"));
    }

    private boolean verifyPayment(PaidPayment paidPayment) {

        PaymentCustomDataCommand paymentCustomDataCommand = parseToPaymentCustomData(paidPayment.getCustomData());

        if (paymentCustomDataCommand == null) {
            return false;
        }

        return verifyPaymentAmount(paymentCustomDataCommand, paidPayment.getAmount());
    }

    private PaymentCustomDataCommand parseToPaymentCustomData(String customData) {

        return PaymentCustomDataCommand.from(customData);
    }

    private Boolean verifyPaymentAmount(PaymentCustomDataCommand paymentCustomDataCommand, PaymentAmount paymentAmount) {

        List<Seat> getSeats = seatRepository.findByIdInWithSeatGrade(
                paymentCustomDataCommand.getSeatIdxes().stream().map(idx -> Seat.builder().idx(idx).build()).toList()
        );

        Long actualPrice = getSeats.stream().mapToLong(seat -> seat.getSeatGrade().getPrice()).sum();
        log.info("actualPrice = {} ", actualPrice);
        log.info("paymentAmount.getTotal() = {}", paymentAmount.getTotal());

        return actualPrice.equals(paymentAmount.getTotal());
    }
}
