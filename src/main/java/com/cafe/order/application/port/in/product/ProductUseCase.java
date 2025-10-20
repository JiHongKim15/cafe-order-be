package com.cafe.order.application.port.in.product;

import com.cafe.order.domain.product.model.Product;

import java.util.List;

public interface ProductUseCase {
    List<Product> findProductsByIds(List<Long> productIds);
}
