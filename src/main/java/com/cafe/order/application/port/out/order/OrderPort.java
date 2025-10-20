package com.cafe.order.application.port.out.order;

import com.cafe.order.domain.order.model.Order;

import java.util.Optional;

public interface OrderPort {
    Order save(Order order);
    Optional<Order> findById(Long orderId);
}
