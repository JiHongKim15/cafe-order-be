package com.cafe.order.domain.order.service;

import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.model.enums.Gender;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import com.cafe.order.domain.order.model.Order;
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


@DisplayName("OrderDomainService 도메인 규칙 테스트")
class OrderDomainServiceTest {

    private OrderDomainService orderDomainService;

    @BeforeEach
    void setUp() {
        orderDomainService = new OrderDomainService();
    }

    // ========== 주문 생성 검증 시나리오 ==========

    @Test
    @DisplayName("시나리오: 활성 회원이 상품을 주문하면 검증 통과")
    void validateOrderCreation_Success_WhenActiveMemberOrdersProducts() {
        // Given: 활성 상태의 회원
        Member activeMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.ACTIVE)
                .build();

        // And: 주문할 상품 목록
        List<Product> products = List.of(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build(),
                Product.builder().id(2L).name("카페라떼").price(BigDecimal.valueOf(5000)).build()
        );

        // When & Then: 주문 생성 검증이 예외 없이 통과
        assertThatCode(() -> orderDomainService.validateOrderCreation(activeMember, products))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("시나리오: 탈퇴한 회원이 주문 시도하면 실패")
    void validateOrderCreation_Fail_WhenWithdrawnMemberTriesToOrder() {
        // Given: 탈퇴 상태의 회원
        Member withdrawnMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.WITHDRAWN)
                .build();

        // And: 주문할 상품 목록
        List<Product> products = List.of(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build()
        );

        // When & Then: 탈퇴 회원은 주문할 수 없음
        assertThatThrownBy(() -> orderDomainService.validateOrderCreation(withdrawnMember, products))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("회원만 주문할 수 있습니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("시나리오: 주문할 상품이 없으면 실패")
    void validateOrderCreation_Fail_WhenNoProductsToOrder() {
        // Given: 활성 상태의 회원
        Member activeMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.ACTIVE)
                .build();

        // And: 빈 상품 목록
        List<Product> emptyProducts = Collections.emptyList();

        // When & Then: 주문할 상품이 없으면 실패
        assertThatThrownBy(() -> orderDomainService.validateOrderCreation(activeMember, emptyProducts))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("주문할 상품이 없습니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("시나리오: 상품 목록이 null이면 실패")
    void validateOrderCreation_Fail_WhenProductsIsNull() {
        // Given: 활성 상태의 회원
        Member activeMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.ACTIVE)
                .build();

        // When & Then: 상품 목록이 null이면 실패
        assertThatThrownBy(() -> orderDomainService.validateOrderCreation(activeMember, null))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("주문할 상품이 없습니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REQUEST);
    }

    // ========== 주문 취소 검증 시나리오 ==========

    @Test
    @DisplayName("시나리오: 확정된 주문을 취소하면 검증 통과")
    void validateOrderCancellation_Success_WhenCancellingConfirmedOrder() {
        // Given: 확정 상태의 주문
        Order confirmedOrder = Order.builder()
                .id(1L)
                .memberId(1L)
                .productIds(List.of(1L, 2L))
                .paymentId("payment-123")
                .status(OrderStatus.CONFIRMED)
                .build();

        // When & Then: 주문 취소 검증이 예외 없이 통과
        assertThatCode(() -> orderDomainService.validateOrderCancellation(confirmedOrder))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("시나리오: 이미 취소된 주문을 다시 취소하려고 하면 실패")
    void validateOrderCancellation_Fail_WhenOrderAlreadyCancelled() {
        // Given: 이미 취소된 주문
        Order cancelledOrder = Order.builder()
                .id(1L)
                .memberId(1L)
                .productIds(List.of(1L, 2L))
                .paymentId("payment-123")
                .status(OrderStatus.CANCELLED)
                .build();

        // When & Then: 이미 취소된 주문은 다시 취소할 수 없음
        assertThatThrownBy(() -> orderDomainService.validateOrderCancellation(cancelledOrder))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("이미 취소된 주문입니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("시나리오: 고객이 주문 직후 바로 취소하는 경우")
    void validateOrderCancellation_Success_CustomerCancelsRightAfterOrder() {
        // Given: 방금 생성된 주문
        Order newOrder = Order.create(1L, List.of(1L, 2L), "payment-123");

        // When & Then: 바로 취소 가능
        assertThatCode(() -> orderDomainService.validateOrderCancellation(newOrder))
                .doesNotThrowAnyException();
    }
}