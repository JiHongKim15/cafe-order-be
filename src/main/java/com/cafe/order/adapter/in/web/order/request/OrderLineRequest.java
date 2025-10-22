package com.cafe.order.adapter.in.web.order.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "주문 상품 정보")
public record OrderLineRequest(
    @Schema(description = "상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "상품 ID는 필수입니다.")
    @Positive(message = "상품 ID는 양수여야 합니다.")
    Long productId,

    @Schema(description = "수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "수량은 필수입니다.")
    @Positive(message = "수량은 1 이상이어야 합니다.")
    Integer quantity
) {
}
