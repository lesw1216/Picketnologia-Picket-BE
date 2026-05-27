package com.picketlogia.picket.api.payments.service;

import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.webhook.Webhook;
import io.portone.sdk.server.webhook.WebhookTransaction;
import io.portone.sdk.server.webhook.WebhookVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebhookService {

    private final WebhookVerifier portoneWebhook;
    private final PaymentService paymentService;

    public WebhookService(PaymentService paymentService, @Value("${portone.key.webhook}") String secretKey) {
        this.portoneWebhook = new WebhookVerifier(secretKey);
        this.paymentService = paymentService;
    }

    /**
     * PortOne 웹훅 요청을 서명 검증한 뒤 결제 완료 처리를 위임한다.
     *
     * @param body             웹훅 요청 본문
     * @param webhookId        웹훅 ID
     * @param webhookTimestamp 웹훅 타임스탬프
     * @param webhookSignature 검증용 서명
     * @throws BaseException 서명 검증에 실패했을 때
     */
    public void handleWebhook(String body, String webhookId, String webhookTimestamp, String webhookSignature) {

        Webhook webhook;

        try {
            webhook = portoneWebhook.verify(body, webhookId, webhookSignature, webhookTimestamp);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BaseException.from(BaseResponseStatus.FAILED_PAYMENT_WEBHOOK);
        }

        if (webhook instanceof WebhookTransaction transaction) {
            paymentService.completePayment(transaction.getData().getPaymentId());
        }
    }
}
