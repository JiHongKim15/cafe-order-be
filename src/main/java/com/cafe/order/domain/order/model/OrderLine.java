package com.cafe.order.domain.order.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderLine {
    private Long productId;
    private Integer quantity;

    public static OrderLine of(Long productId, Integer quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("productId는 필수입니다");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다");
        }

        return OrderLine.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}
