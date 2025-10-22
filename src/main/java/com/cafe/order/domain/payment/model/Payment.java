package com.cafe.order.domain.payment.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Payment {

    private Long id;
    private String paymentId;
    private Long orderId;
    private LocalDateTime paymentDateTime;

    public static Payment createPayment(String paymentId, Long orderId) {
        return Payment.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .paymentDateTime(LocalDateTime.now())
                .build();
    }

}
