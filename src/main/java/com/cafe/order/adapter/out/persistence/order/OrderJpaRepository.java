package com.cafe.order.adapter.out.persistence.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    @Query("SELECT o FROM OrderJpaEntity o " +
            "JOIN FETCH o.orderLines " +
            "WHERE o.id = :orderId")
    Optional<OrderJpaEntity> findByIdWithDetails(@Param("orderId") Long orderId);
}
