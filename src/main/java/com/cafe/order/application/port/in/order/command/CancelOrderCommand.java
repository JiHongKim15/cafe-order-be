package com.cafe.order.application.port.in.order.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CancelOrderCommand(
    @NotNull(message = "주문 ID는 필수입니다.")
    @Positive(message = "주문 ID는 양수여야 합니다.")
    Long orderId
) {}
