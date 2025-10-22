package com.cafe.order.application.port.in.payment.command;

import jakarta.validation.constraints.NotBlank;

public record CancelPaymentCommand(
        @NotBlank(message = "결제 ID는 필수입니다.")
        String paymentId
) {
}
