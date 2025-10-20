package com.cafe.order.application.port.in.payment;

import java.util.concurrent.CompletableFuture;

public interface PaymentUseCase {

    String processPaymentSync();
    void cancelPaymentSync(String paymentId);
}
