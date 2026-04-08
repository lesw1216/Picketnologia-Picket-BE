package com.picketlogia.picket.api.payments.controller;

import com.picketlogia.picket.api.payments.dto.result.PaymentStatusResult;
import com.picketlogia.picket.api.payments.service.WebhookService;
import com.picketlogia.picket.api.reservation.service.ReservationService;
import com.picketlogia.picket.api.user.dto.request.UserAuthRequest;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentsController {

    private final WebhookService webHookService;
    private final ReservationService reservationService;

    @PostMapping("/payment/webhook")
    public ResponseEntity<BaseResponse<Object>> validPayment(@RequestBody String body,
                                                             @RequestHeader("webhook-id") String webhookId,
                                                             @RequestHeader("webhook-timestamp") String webhookTimestamp,
                                                             @RequestHeader("webhook-signature") String webhookSignature) {

        webHookService.handleWebhook(body, webhookId, webhookTimestamp, webhookSignature);
        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @GetMapping("/payment/{paymentId}/status")
    public ResponseEntity<BaseResponse<Object>> getPaymentStatus(@PathVariable String paymentId,
                                                                 @AuthenticationPrincipal UserAuthRequest loginUser) {

        PaymentStatusResult paymentStatusResult = reservationService.findPaymentStatusOfReservation(paymentId, loginUser.getIdx());
        return ResponseEntity.ok(BaseResponse.success(paymentStatusResult));
    }
}