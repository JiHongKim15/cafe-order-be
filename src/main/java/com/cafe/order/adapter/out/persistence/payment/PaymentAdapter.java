package com.cafe.order.adapter.out.persistence.payment;

import com.cafe.order.application.port.out.payment.PaymentPort;
import com.cafe.order.domain.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentAdapter implements PaymentPort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentPersistenceMapper paymentPersistenceMapper;

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = paymentPersistenceMapper.toEntity(payment);
        PaymentJpaEntity savedEntity = paymentJpaRepository.save(entity);
        return paymentPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findById(Long paymentId) {
        return paymentJpaRepository.findById(paymentId)
                .map(paymentPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByPaymentId(String paymentId) {
        return paymentJpaRepository.findByPaymentId(paymentId)
                .map(paymentPersistenceMapper::toDomain);
    }
}
