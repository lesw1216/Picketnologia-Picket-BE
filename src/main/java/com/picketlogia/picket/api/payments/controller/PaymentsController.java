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

    /**
     * PortOne에서 호출하는 결제 웹훅 요청을 검증하고 결제 완료 처리를 위임한다.
     *
     * @param body             웹훅 요청 본문
     * @param webhookId        웹훅 ID 헤더
     * @param webhookTimestamp 웹훅 타임스탬프 헤더
     * @param webhookSignature 웹훅 서명 헤더
     * @return 빈 본문의 표준 응답
     */
    @PostMapping("/payment/webhook")
    public ResponseEntity<BaseResponse<Object>> validPayment(@RequestBody String body,
                                                             @RequestHeader("webhook-id") String webhookId,
                                                             @RequestHeader("webhook-timestamp") String webhookTimestamp,
                                                             @RequestHeader("webhook-signature") String webhookSignature) {

        webHookService.handleWebhook(body, webhookId, webhookTimestamp, webhookSignature);

        return ResponseEntity.ok(BaseResponse.success(null));
    }

    /**
     * 결제 ID와 사용자 ID로 예매의 현재 결제 상태를 조회한다.
     *
     * @param paymentId 결제 ID
     * @param loginUser 요청자 인증 정보
     * @return 결제 상태 결과를 담은 표준 응답
     */
    @GetMapping("/payment/{paymentId}/status")
    public ResponseEntity<BaseResponse<Object>> getPaymentStatus(@PathVariable String paymentId,
                                                                 @AuthenticationPrincipal UserAuthRequest loginUser) {

        PaymentStatusResult paymentStatusResult = reservationService.findPaymentStatusOfReservation(paymentId, loginUser.getIdx());

        return ResponseEntity.ok(BaseResponse.success(paymentStatusResult));
    }
}
