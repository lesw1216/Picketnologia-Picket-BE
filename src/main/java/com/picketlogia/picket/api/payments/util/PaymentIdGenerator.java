package com.picketlogia.picket.api.payments.util;

import java.util.UUID;

public class PaymentIdGenerator {

    public static String generatePaymentId() {
        return UUID.randomUUID().toString();
    }
}
