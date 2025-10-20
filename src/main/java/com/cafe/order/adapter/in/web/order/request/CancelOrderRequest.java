package com.cafe.order.adapter.in.web.order.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "주문 취소 요청")
public record CancelOrderRequest(
    @Schema(description = "취소할 주문 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "주문 ID는 필수입니다.")
    @Positive(message = "주문 ID는 양수여야 합니다.")
    Long orderId
) {
}
