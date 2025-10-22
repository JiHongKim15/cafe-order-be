package com.cafe.order.application.service.payment;

import com.cafe.order.application.port.in.payment.PaymentCommandUseCase;
import com.cafe.order.application.port.in.payment.command.CancelPaymentCommand;
import com.cafe.order.application.port.in.payment.command.ProcessPaymentCommand;
import com.cafe.order.application.port.out.payment.ExternalPaymentPort;
import com.cafe.order.application.port.out.payment.PaymentPort;
import com.cafe.order.domain.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class PaymentCommandService implements PaymentCommandUseCase {

    private final PaymentPort paymentPort;
    private final ExternalPaymentPort externalPaymentPort;

    @Override
    public Payment processPayment(ProcessPaymentCommand command) {
        String externalPaymentId = externalPaymentPort.processPayment();

        Payment successPayment = Payment.createPayment(
                externalPaymentId,
                command.orderId()
        );

        Payment savedPayment = paymentPort.save(successPayment);
        log.info("결제 처리 완료: paymentId={}, externalPaymentId={}, orderId={}",
                savedPayment.getId(), externalPaymentId, command.orderId());

        return savedPayment;
    }

    @Override
    public void cancelPayment(CancelPaymentCommand command) {
        externalPaymentPort.cancelPayment(command.paymentId());
        log.info("결제 취소 완료: externalPaymentId={}", command.paymentId());
    }
}
