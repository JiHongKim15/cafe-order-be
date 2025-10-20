package com.cafe.order.domain.order.model;

import com.cafe.order.domain.order.model.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class Order {
    private Long id;
    private Long memberId;
    private List<Long> productIds;
    private OrderStatus status;
    private String paymentId;
    private LocalDateTime orderDateTime;
    private LocalDateTime cancelDateTime;

    public static Order create(
            Long memberId,
            List<Long> productIds,
            String paymentId
    ) {
        return Order.builder()
                .memberId(memberId)
                .productIds(productIds)
                .status(OrderStatus.CONFIRMED)
                .paymentId(paymentId)
                .orderDateTime(LocalDateTime.now())
                .build();
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        this.cancelDateTime = LocalDateTime.now();
    }

    public boolean isCancelled() {
        return this.status == OrderStatus.CANCELLED;
    }

    public boolean isConfirmed() {
        return this.status == OrderStatus.CONFIRMED;
    }
}