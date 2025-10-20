package com.cafe.order.application.port.out.product;

import com.cafe.order.domain.product.model.Product;

import java.util.List;

public interface ProductPort {
    List<Product> findByIds(List<Long> productIds);
}
