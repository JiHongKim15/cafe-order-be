package com.cafe.order.application.port.out.payment;

public interface PaymentPort {
    String processPayment();
    String cancelPayment(String paymentId);
}
