package com.cafe.order.application.port.in.order.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderCommand(
    @NotNull(message = "회원 ID는 필수입니다.")
    @Positive(message = "회원 ID는 양수여야 합니다.")
    Long memberId,

    @NotEmpty(message = "주문 상품 목록은 필수입니다.")
    List<@Valid OrderLineCommand> orderLines
) {}
