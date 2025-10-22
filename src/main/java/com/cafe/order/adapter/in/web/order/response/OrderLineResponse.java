package com.cafe.order.adapter.in.web.order.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 상품 정보")
public record OrderLineResponse(
    @Schema(description = "상품 ID", example = "1")
    Long productId,

    @Schema(description = "수량", example = "2")
    Integer quantity
) {
}
