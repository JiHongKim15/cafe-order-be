package com.cafe.order.domain.order.service;

import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.model.enums.Gender;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import com.cafe.order.domain.order.model.Order;
import com.cafe.order.domain.order.model.OrderLine;
import com.cafe.order.domain.order.model.enums.OrderStatus;
import com.cafe.order.domain.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderDomainService 테스트")
class OrderDomainServiceTest {

    private OrderDomainService orderDomainService;

    @BeforeEach
    void setUp() {
        orderDomainService = new OrderDomainService();
    }

    // ========== 주문 생성 검증 ==========

    @Test
    @DisplayName("주문 생성 검증 성공")
    void validateOrderCreation_Success() {
        // Given
        Member activeMember = Member.builder()
                .id(1L)
                .status(MemberStatus.ACTIVE)
                .build();

        List<Product> products = List.of(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build()
        );

        // When & Then
        assertThatCode(() -> orderDomainService.validateOrderCreation(activeMember, products))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("주문 생성 검증 실패 - 탈퇴한 회원")
    void validateOrderCreation_Fail_WithdrawnMember() {
        // Given
        Member withdrawnMember = Member.builder()
                .id(1L)
                .status(MemberStatus.WITHDRAWN)
                .build();

        List<Product> products = List.of(
                Product.builder().id(1L).name("아메리카노").build()
        );

        // When & Then
        assertThatThrownBy(() -> orderDomainService.validateOrderCreation(withdrawnMember, products))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_MEMBER_NOT_ACTIVE);
    }

    @Test
    @DisplayName("주문 생성 검증 실패 - 빈 상품 목록")
    void validateOrderCreation_Fail_EmptyProducts() {
        // Given
        Member activeMember = Member.builder()
                .id(1L)
                .status(MemberStatus.ACTIVE)
                .build();

        // When & Then
        assertThatThrownBy(() -> orderDomainService.validateOrderCreation(activeMember, Collections.emptyList()))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_EMPTY_PRODUCTS);
    }

    // ========== 주문 취소 검증 ==========

    @Test
    @DisplayName("주문 취소 검증 성공")
    void validateOrderCancellation_Success() {
        // Given
        Order confirmedOrder = Order.builder()
                .id(1L)
                .memberId(1L)
                .orderLines(List.of())
                .paymentId("payment-123")
                .status(OrderStatus.CONFIRMED)
                .build();

        // When & Then
        assertThatCode(() -> orderDomainService.validateOrderCancellation(confirmedOrder))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("주문 취소 검증 실패 - 이미 취소된 주문")
    void validateOrderCancellation_Fail_AlreadyCancelled() {
        // Given
        Order cancelledOrder = Order.builder()
                .id(1L)
                .memberId(1L)
                .orderLines(List.of())
                .paymentId("payment-123")
                .status(OrderStatus.CANCELLED)
                .build();

        // When & Then
        assertThatThrownBy(() -> orderDomainService.validateOrderCancellation(cancelledOrder))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_ALREADY_CANCELLED);
    }
}
