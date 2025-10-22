package com.cafe.order.application.port.out.payment;


public interface ExternalPaymentPort {
    String processPayment();
    void cancelPayment(String paymentId);
}
