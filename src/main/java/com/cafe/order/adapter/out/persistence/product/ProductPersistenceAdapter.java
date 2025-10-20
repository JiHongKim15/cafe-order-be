package com.cafe.order.adapter.out.persistence.product;

import com.cafe.order.application.port.out.product.ProductPort;
import com.cafe.order.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPort {

    private final ProductJpaRepository productJpaRepository;
    private final ProductPersistenceMapper productPersistenceMapper;

    @Override
    public List<Product> findByIds(List<Long> productIds) {
        return productJpaRepository.findByIdIn(productIds)
                .stream()
                .map(productPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
