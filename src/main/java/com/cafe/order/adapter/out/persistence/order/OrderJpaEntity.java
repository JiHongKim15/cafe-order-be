package com.cafe.order.adapter.out.persistence.order;

import com.cafe.order.adapter.out.persistence.common.BaseJpaEntity;
import com.cafe.order.adapter.out.persistence.member.MemberJpaEntity;
import com.cafe.order.domain.order.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberJpaEntity member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderLineJpaEntity> orderLines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, length = 100)
    private String paymentId;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    private LocalDateTime cancelDateTime;
}
