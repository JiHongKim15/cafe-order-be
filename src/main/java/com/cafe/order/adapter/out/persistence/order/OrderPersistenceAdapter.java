package com.cafe.order.adapter.out.persistence.order;

import com.cafe.order.application.port.out.order.OrderPort;
import com.cafe.order.domain.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderPort {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderPersistenceMapper orderPersistenceMapper;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = orderPersistenceMapper.toEntity(order);
        OrderJpaEntity savedEntity = orderJpaRepository.save(entity);
        return orderPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderJpaRepository.findByIdWithDetails(orderId)
                .map(orderPersistenceMapper::toDomain);
    }
}
