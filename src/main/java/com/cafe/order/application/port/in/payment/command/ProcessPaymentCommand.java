package com.cafe.order.application.port.in.payment.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProcessPaymentCommand(
        @NotNull(message = "주문 ID는 필수입니다.")
        @Positive(message = "주문 ID는 양수여야 합니다.")
        Long orderId
) {
}
