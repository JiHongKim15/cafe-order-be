package com.cafe.order.application.service.order;

import com.cafe.order.application.port.in.member.MemberUseCase;
import com.cafe.order.application.port.in.order.command.CancelOrderCommand;
import com.cafe.order.application.port.in.order.command.CreateOrderCommand;
import com.cafe.order.application.port.in.payment.PaymentUseCase;
import com.cafe.order.application.port.in.product.ProductUseCase;
import com.cafe.order.application.port.out.order.OrderPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import com.cafe.order.domain.order.model.Order;
import com.cafe.order.domain.order.model.enums.OrderStatus;
import com.cafe.order.domain.order.service.OrderDomainService;
import com.cafe.order.domain.product.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

/**
 * OrderService 단위 테스트
 *
 * 테스트 전략:
 * - Mockito를 사용한 단위 테스트
 * - 비즈니스 로직 플로우에 집중
 * - Given-When-Then 패턴 사용
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderPort orderPort;

    @Mock
    private MemberUseCase memberUseCase;

    @Mock
    private ProductUseCase productUseCase;

    @Mock
    private PaymentUseCase paymentUseCase;

    @Mock
    private OrderDomainService orderDomainService;

    @Test
    @DisplayName("주문 생성 성공 - 정상 플로우")
    void createOrder_Success() {
        // Given
        Long memberId = 1L;
        List<Long> productIds = List.of(1L, 2L);
        CreateOrderCommand command = new CreateOrderCommand(memberId, productIds);

        Member member = Member.builder()
                .id(memberId)
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .status(MemberStatus.ACTIVE)
                .build();

        List<Product> products = List.of(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build(),
                Product.builder().id(2L).name("라떼").price(BigDecimal.valueOf(5000)).build()
        );

        String paymentId = "payment-123";

        Order savedOrder = Order.builder()
                .id(1L)
                .memberId(memberId)
                .productIds(productIds)
                .paymentId(paymentId)
                .status(OrderStatus.CONFIRMED)
                .build();

        given(memberUseCase.findById(memberId)).willReturn(member);
        given(productUseCase.findProductsByIds(productIds)).willReturn(products);
        willDoNothing().given(orderDomainService).validateOrderCreation(member, products);
        given(paymentUseCase.processPaymentSync()).willReturn(paymentId);
        given(orderPort.save(any(Order.class))).willReturn(savedOrder);

        // When
        Order result = orderService.createOrder(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPaymentId()).isEqualTo(paymentId);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        then(memberUseCase).should().findById(memberId);
        then(productUseCase).should().findProductsByIds(productIds);
        then(orderDomainService).should().validateOrderCreation(member, products);
        then(paymentUseCase).should().processPaymentSync();
        then(orderPort).should().save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - 존재하지 않는 상품")
    void createOrder_Fail_ProductNotFound() {
        // Given
        Long memberId = 1L;
        List<Long> productIds = List.of(1L, 2L, 3L);
        CreateOrderCommand command = new CreateOrderCommand(memberId, productIds);

        Member member = Member.builder().id(memberId).name("홍길동").phoneNumber("010-1234-5678").status(MemberStatus.ACTIVE).build();

        given(memberUseCase.findById(memberId)).willReturn(member);
        // ProductService에서 검증하고 예외를 던짐
        given(productUseCase.findProductsByIds(productIds))
                .willThrow(new BizException(ErrorCode.PRODUCT_NOT_FOUND, "존재하지 않는 상품이 포함되어 있습니다."));

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(command))
                .isInstanceOf(BizException.class)
                .hasMessage("존재하지 않는 상품이 포함되어 있습니다.");

        then(paymentUseCase).should(never()).processPaymentSync();
        then(orderPort).should(never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - 도메인 검증 실패 (비활성 회원)")
    void createOrder_Fail_InvalidMember() {
        // Given
        Long memberId = 1L;
        List<Long> productIds = List.of(1L);
        CreateOrderCommand command = new CreateOrderCommand(memberId, productIds);

        Member member = Member.builder().id(memberId).name("홍길동").phoneNumber("010-1234-5678").status(MemberStatus.ACTIVE).build();

        List<Product> products = List.of(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build()
        );

        given(memberUseCase.findById(memberId)).willReturn(member);
        given(productUseCase.findProductsByIds(productIds)).willReturn(products);
        willThrow(new BizException(ErrorCode.INVALID_REQUEST, "회원만 주문할 수 있습니다."))
                .given(orderDomainService).validateOrderCreation(member, products);

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(command))
                .isInstanceOf(BizException.class)
                .hasMessage("회원만 주문할 수 있습니다.");

        then(paymentUseCase).should(never()).processPaymentSync();
        then(orderPort).should(never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - 결제 실패")
    void createOrder_Fail_PaymentFailed() {
        // Given
        Long memberId = 1L;
        List<Long> productIds = List.of(1L);
        CreateOrderCommand command = new CreateOrderCommand(memberId, productIds);

        Member member = Member.builder().id(memberId).name("홍길동").phoneNumber("010-1234-5678").status(MemberStatus.ACTIVE).build();

        List<Product> products = List.of(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build()
        );

        given(memberUseCase.findById(memberId)).willReturn(member);
        given(productUseCase.findProductsByIds(productIds)).willReturn(products);
        willDoNothing().given(orderDomainService).validateOrderCreation(member, products);
        given(paymentUseCase.processPaymentSync())
                .willThrow(new BizException(ErrorCode.PAYMENT_FAILED, "결제 처리에 실패했습니다."));

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(command))
                .isInstanceOf(BizException.class)
                .hasMessage("결제 처리에 실패했습니다.");

        then(orderPort).should(never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 취소 성공 - 정상 플로우")
    void cancelOrder_Success() {
        // Given
        Long orderId = 1L;
        String paymentId = "payment-123";
        CancelOrderCommand command = new CancelOrderCommand(orderId);

        Order order = Order.builder()
                .id(orderId)
                .memberId(1L)
                .productIds(List.of(1L))
                .paymentId(paymentId)
                .status(OrderStatus.CONFIRMED)
                .build();

        given(orderPort.findById(orderId)).willReturn(Optional.of(order));
        willDoNothing().given(orderDomainService).validateOrderCancellation(order);
        willDoNothing().given(paymentUseCase).cancelPaymentSync(paymentId);
        given(orderPort.save(any(Order.class))).willReturn(order);

        // When
        orderService.cancelOrder(command);

        // Then
        then(orderPort).should().findById(orderId);
        then(orderDomainService).should().validateOrderCancellation(order);
        then(paymentUseCase).should().cancelPaymentSync(paymentId);
        then(orderPort).should().save(order);
    }

    @Test
    @DisplayName("주문 취소 실패 - 주문을 찾을 수 없음")
    void cancelOrder_Fail_OrderNotFound() {
        // Given
        Long orderId = 999L;
        CancelOrderCommand command = new CancelOrderCommand(orderId);

        given(orderPort.findById(orderId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.cancelOrder(command))
                .isInstanceOf(BizException.class)
                .hasMessage("주문을 찾을 수 없습니다.");

        then(paymentUseCase).should(never()).cancelPaymentSync(any());
        then(orderPort).should(never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 취소 실패 - 이미 취소된 주문")
    void cancelOrder_Fail_AlreadyCancelled() {
        // Given
        Long orderId = 1L;
        CancelOrderCommand command = new CancelOrderCommand(orderId);

        Order order = Order.builder()
                .id(orderId)
                .memberId(1L)
                .productIds(List.of(1L))
                .paymentId("payment-123")
                .status(OrderStatus.CANCELLED)
                .build();

        given(orderPort.findById(orderId)).willReturn(Optional.of(order));
        willThrow(new BizException(ErrorCode.INVALID_REQUEST, "이미 취소된 주문입니다."))
                .given(orderDomainService).validateOrderCancellation(order);

        // When & Then
        assertThatThrownBy(() -> orderService.cancelOrder(command))
                .isInstanceOf(BizException.class)
                .hasMessage("이미 취소된 주문입니다.");

        then(paymentUseCase).should(never()).cancelPaymentSync(any());
        then(orderPort).should(never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 취소 실패 - 결제 취소 실패")
    void cancelOrder_Fail_PaymentCancelFailed() {
        // Given
        Long orderId = 1L;
        String paymentId = "payment-123";
        CancelOrderCommand command = new CancelOrderCommand(orderId);

        Order order = Order.builder()
                .id(orderId)
                .memberId(1L)
                .productIds(List.of(1L))
                .paymentId(paymentId)
                .status(OrderStatus.CONFIRMED)
                .build();

        given(orderPort.findById(orderId)).willReturn(Optional.of(order));
        willDoNothing().given(orderDomainService).validateOrderCancellation(order);
        willThrow(new BizException(ErrorCode.PAYMENT_FAILED, "결제 취소에 실패했습니다."))
                .given(paymentUseCase).cancelPaymentSync(paymentId);

        // When & Then
        assertThatThrownBy(() -> orderService.cancelOrder(command))
                .isInstanceOf(BizException.class)
                .hasMessage("결제 취소에 실패했습니다.");

        then(orderPort).should(never()).save(any(Order.class));
    }
}