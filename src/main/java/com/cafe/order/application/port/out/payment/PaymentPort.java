package com.cafe.order.application.port.out.payment;

import com.cafe.order.domain.payment.model.Payment;

import java.util.Optional;

/**
 * 결제 정보 영속화를 위한 Output Port
 */
public interface PaymentPort {

    Payment save(Payment payment);

    Optional<Payment> findById(Long paymentId);

    Optional<Payment> findByPaymentId(String paymentId);
}
