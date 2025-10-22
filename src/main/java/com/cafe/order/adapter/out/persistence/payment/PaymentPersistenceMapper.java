package com.cafe.order.adapter.out.persistence.payment;

import com.cafe.order.domain.payment.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentPersistenceMapper {

    public PaymentJpaEntity toEntity(Payment payment) {
        return PaymentJpaEntity.builder()
                .id(payment.getId())
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .paymentDateTime(payment.getPaymentDateTime())
                .build();
    }

    public Payment toDomain(PaymentJpaEntity entity) {
        return Payment.builder()
                .id(entity.getId())
                .paymentId(entity.getPaymentId())
                .orderId(entity.getOrderId())
                .paymentDateTime(entity.getPaymentDateTime())
                .build();
    }
}
