package com.cafe.order.adapter.out.persistence.order;

import com.cafe.order.adapter.out.persistence.member.MemberJpaEntity;
import com.cafe.order.adapter.out.persistence.member.MemberJpaRepository;
import com.cafe.order.adapter.out.persistence.product.ProductJpaEntity;
import com.cafe.order.adapter.out.persistence.product.ProductJpaRepository;
import com.cafe.order.domain.order.model.Order;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderPersistenceMapper {

    private final MemberJpaRepository memberJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    public OrderJpaEntity toEntity(Order order) {
        MemberJpaEntity memberEntity = memberJpaRepository.findById(order.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + order.getMemberId()));

        OrderJpaEntity orderEntity = OrderJpaEntity.builder()
                .id(order.getId())
                .member(memberEntity)
                .status(order.getStatus())
                .paymentId(order.getPaymentId())
                .orderDateTime(order.getOrderDateTime())
                .cancelDateTime(order.getCancelDateTime())
                .build();

        Map<Long, Long> productIdToQuantityMap = order.getProductIds().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<ProductJpaEntity> productEntities = productJpaRepository.findByIdIn(new ArrayList<>(productIdToQuantityMap.keySet()));

        if (productEntities.size() != productIdToQuantityMap.size()) {
            throw new EntityNotFoundException("One or more products not found.");
        }

        Map<Long, ProductJpaEntity> productIdToEntityMap = productEntities.stream()
                .collect(Collectors.toMap(ProductJpaEntity::getId, Function.identity()));

        List<OrderLineJpaEntity> orderLines = productIdToQuantityMap.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    int quantity = entry.getValue().intValue();
                    ProductJpaEntity productEntity = productIdToEntityMap.get(productId);

                    return OrderLineJpaEntity.builder()
                            .order(orderEntity)
                            .product(productEntity)
                            .quantity(quantity)
                            .orderPrice(productEntity.getPrice())
                            .build();
                })
                .toList();

        orderEntity.getOrderLines().addAll(orderLines);

        return orderEntity;
    }

    public Order toDomain(OrderJpaEntity entity) {
        List<Long> productIds = entity.getOrderLines().stream()
                .flatMap(line -> Collections.nCopies(line.getQuantity(), line.getProduct().getId()).stream())
                .collect(Collectors.toList());

        return Order.builder()
                .id(entity.getId())
                .memberId(entity.getMember().getId())
                .productIds(productIds)
                .status(entity.getStatus())
                .paymentId(entity.getPaymentId())
                .orderDateTime(entity.getOrderDateTime())
                .cancelDateTime(entity.getCancelDateTime())
                .build();
    }
}