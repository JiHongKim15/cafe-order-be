package com.cafe.order.application.port.in.order.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderLineCommand(
    @NotNull(message = "상품 ID는 필수입니다.")
    @Positive(message = "상품 ID는 양수여야 합니다.")
    Long productId,

    @NotNull(message = "수량은 필수입니다.")
    @Positive(message = "수량은 1 이상이어야 합니다.")
    Integer quantity
) {
}
