package com.cafe.order.adapter.in.web.order.response;

import com.cafe.order.domain.order.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderResponse(
    Long orderId,
    Long memberId,
    List<Long> productIds,
    OrderStatus status,
    String paymentId,
    LocalDateTime orderDateTime
) {
}
