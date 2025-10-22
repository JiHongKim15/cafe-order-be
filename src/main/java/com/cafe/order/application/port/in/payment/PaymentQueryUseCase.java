package com.cafe.order.application.port.in.payment;

import com.cafe.order.domain.payment.model.Payment;

public interface PaymentQueryUseCase {

    Payment findById(Long paymentId);
}
