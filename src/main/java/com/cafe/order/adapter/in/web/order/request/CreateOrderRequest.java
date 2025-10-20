package com.cafe.order.adapter.in.web.order.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "상품 ID 목록 (1: 아메리카노, 2: 카페라떼, 3: 카푸치노, 4: 바닐라라떼, 5: 카라멜마키아또, 6: 에스프레소, 7: 핫초코, 8: 녹차라떼)",
            example = "[1, 2, 3]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "상품 ID 목록은 필수입니다.")
    List<@NotNull @Positive Long> productIds
) {
}
