package com.cafe.order.adapter.out.persistence.order;

import com.cafe.order.domain.order.model.Order;
import com.cafe.order.domain.order.model.OrderLine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderPersistenceMapper {

    public OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity orderEntity = OrderJpaEntity.builder()
                .id(order.getId())
                .memberId(order.getMemberId())
                .status(order.getStatus())
                .paymentId(order.getPaymentId())
                .orderDateTime(order.getOrderDateTime())
                .cancelDateTime(order.getCancelDateTime())
                .build();

        List<OrderLineJpaEntity> orderLineEntities = order.getOrderLines().stream()
                .map(orderLine -> OrderLineJpaEntity.builder()
                        .orderId(orderEntity.getId())
                        .productId(orderLine.getProductId())
                        .quantity(orderLine.getQuantity())
                        .build())
                .toList();

        orderEntity.getOrderLines().addAll(orderLineEntities);

        return orderEntity;
    }

    public Order toDomain(OrderJpaEntity entity) {
        List<OrderLine> orderLines = entity.getOrderLines().stream()
                .map(lineEntity -> OrderLine.builder()
                        .productId(lineEntity.getProductId())
                        .quantity(lineEntity.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return Order.builder()
                .id(entity.getId())
                .memberId(entity.getMemberId())
                .orderLines(orderLines)
                .status(entity.getStatus())
                .paymentId(entity.getPaymentId())
                .orderDateTime(entity.getOrderDateTime())
                .cancelDateTime(entity.getCancelDateTime())
                .build();
    }
}