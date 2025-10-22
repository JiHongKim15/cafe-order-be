package com.cafe.order.adapter.in.web.order.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Schema(description = "주문 생성 요청")
public record CreateOrderRequest(
    @Schema(description = "회원 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "회원 ID는 필수입니다.")
    @Positive(message = "회원 ID는 양수여야 합니다.")
    Long memberId,

    @Schema(description = "주문 상품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "주문 상품 목록은 필수입니다.")
    List<@Valid OrderLineRequest> orderLines
) {
}
