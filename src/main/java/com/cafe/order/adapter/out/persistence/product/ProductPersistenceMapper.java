package com.cafe.order.adapter.out.persistence.product;

import com.cafe.order.domain.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {

    public Product toDomain(ProductJpaEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .build();
    }

    public ProductJpaEntity toEntity(Product domain) {
        return ProductJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .price(domain.getPrice())
                .build();
    }
}
