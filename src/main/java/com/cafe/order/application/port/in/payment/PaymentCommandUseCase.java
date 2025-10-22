package com.cafe.order.application.port.in.payment;

import com.cafe.order.application.port.in.payment.command.CancelPaymentCommand;
import com.cafe.order.application.port.in.payment.command.ProcessPaymentCommand;
import com.cafe.order.domain.payment.model.Payment;
import jakarta.validation.Valid;

public interface PaymentCommandUseCase {

    Payment processPayment(@Valid ProcessPaymentCommand command);

    void cancelPayment(@Valid CancelPaymentCommand command);
}
