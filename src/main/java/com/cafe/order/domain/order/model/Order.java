package com.cafe.order.domain.order.model;

import com.cafe.order.domain.order.model.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class Order {
    private Long id;
    private Long memberId;
    private List<OrderLine> orderLines;
    private OrderStatus status;
    private String paymentId;
    private LocalDateTime orderDateTime;
    private LocalDateTime cancelDateTime;

    public static Order create(
            Long memberId,
            List<OrderLine> orderLines,
            String paymentId
    ) {
        return Order.builder()
                .memberId(memberId)
                .orderLines(orderLines)
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

}