package com.cafe.order.adapter.out.persistence.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    List<ProductJpaEntity> findByIdIn(List<Long> ids);
}
