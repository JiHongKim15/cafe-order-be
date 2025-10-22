package com.cafe.order.adapter.in.web.order.response;

import com.cafe.order.domain.order.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderResponse(
    Long orderId,
    Long memberId,
    List<OrderLineResponse> orderLines,
    OrderStatus status,
    String paymentId,
    LocalDateTime orderDateTime
) {
}
