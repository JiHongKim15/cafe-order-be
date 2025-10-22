package com.cafe.order.application.service.order;

import com.cafe.order.application.port.in.member.MemberQueryUseCase;
import com.cafe.order.application.port.in.order.command.CancelOrderCommand;
import com.cafe.order.application.port.in.order.command.CreateOrderCommand;
import com.cafe.order.application.port.in.order.command.OrderLineCommand;
import com.cafe.order.application.port.in.payment.PaymentCommandUseCase;
import com.cafe.order.application.port.in.payment.command.ProcessPaymentCommand;
import com.cafe.order.application.port.in.product.ProductQueryUseCase;
import com.cafe.order.application.port.out.order.OrderPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import com.cafe.order.domain.order.model.Order;
import com.cafe.order.domain.order.model.OrderLine;
import com.cafe.order.domain.order.model.enums.OrderStatus;
import com.cafe.order.domain.order.service.OrderDomainService;
import com.cafe.order.domain.payment.model.Payment;
import com.cafe.order.domain.product.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderCommandService 테스트")
class OrderCommandServiceTest {

    @InjectMocks
    private OrderCommandCommandService orderCommandService;

    @Mock
    private OrderPort orderPort;

    @Mock
    private MemberQueryUseCase memberQueryUseCase;

    @Mock
    private ProductQueryUseCase productQueryUseCase;

    @Mock
    private PaymentCommandUseCase paymentCommandUseCase;

    @Mock
    private OrderDomainService orderDomainService;

    // ========== 주문 생성 ==========

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_Success() {
        // Given
        Long memberId = 1L;
        List<OrderLineCommand> orderLineCommands = List.of(
                new OrderLineCommand(1L, 2),
                new OrderLineCommand(2L, 1)
        );
        CreateOrderCommand command = new CreateOrderCommand(memberId, orderLineCommands);

        Member member = Member.builder()
                .id(memberId)
                .status(MemberStatus.ACTIVE)
                .build();

        List<Product> products = List.of(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build(),
                Product.builder().id(2L).name("라떼").price(BigDecimal.valueOf(5000)).build()
        );

        Payment payment = Payment.builder()
                .id(1L)
                .paymentId("payment-123")
                .orderId(1L)
                .paymentDateTime(LocalDateTime.now())
                .build();

        Order savedOrder = Order.builder()
                .id(1L)
                .memberId(memberId)
                .orderLines(List.of())
                .paymentId(payment.getPaymentId())
                .status(OrderStatus.CONFIRMED)
                .orderDateTime(LocalDateTime.now())
                .build();

        given(memberQueryUseCase.findById(memberId)).willReturn(member);
        given(productQueryUseCase.findProductsByIds(List.of(1L, 2L))).willReturn(products);
        willDoNothing().given(orderDomainService).validateOrderCreation(member, products);
        given(paymentCommandUseCase.processPayment(any(ProcessPaymentCommand.class))).willReturn(payment);
        given(orderPort.save(any(Order.class))).willReturn(savedOrder);

        // When
        Order result = orderCommandService.createOrder(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("주문 생성 실패 - 탈퇴한 회원")
    void createOrder_Fail_WithdrawnMember() {
        // Given
        Long memberId = 1L;
        CreateOrderCommand command = new CreateOrderCommand(memberId, List.of(new OrderLineCommand(1L, 1)));

        Member withdrawnMember = Member.builder()
                .id(memberId)
                .status(MemberStatus.WITHDRAWN)
                .build();

        List<Product> products = List.of(
                Product.builder().id(1L).name("아메리카노").build()
        );

        given(memberQueryUseCase.findById(memberId)).willReturn(withdrawnMember);
        given(productQueryUseCase.findProductsByIds(List.of(1L))).willReturn(products);
        willThrow(new BizException(ErrorCode.ORDER_MEMBER_NOT_ACTIVE))
                .given(orderDomainService).validateOrderCreation(withdrawnMember, products);

        // When & Then
        assertThatThrownBy(() -> orderCommandService.createOrder(command))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_MEMBER_NOT_ACTIVE);

        then(paymentCommandUseCase).should(never()).processPayment(any());
    }

    // ========== 주문 취소 ==========

    @Test
    @DisplayName("주문 취소 성공")
    void cancelOrder_Success() {
        // Given
        Long orderId = 1L;
        String paymentId = "payment-123";
        CancelOrderCommand command = new CancelOrderCommand(orderId);

        Order order = Order.builder()
                .id(orderId)
                .memberId(1L)
                .orderLines(List.of())
                .status(OrderStatus.CONFIRMED)
                .paymentId(paymentId)
                .build();

        given(orderPort.findById(orderId)).willReturn(Optional.of(order));
        willDoNothing().given(orderDomainService).validateOrderCancellation(order);
        willDoNothing().given(paymentCommandUseCase).cancelPayment(any());
        given(orderPort.save(any(Order.class))).willReturn(order);

        // When
        orderCommandService.cancelOrder(command);

        // Then
        then(orderDomainService).should().validateOrderCancellation(order);
        then(paymentCommandUseCase).should().cancelPayment(any());
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
                .orderLines(List.of())
                .status(OrderStatus.CANCELLED)
                .paymentId("payment-123")
                .cancelDateTime(LocalDateTime.now())
                .build();

        given(orderPort.findById(orderId)).willReturn(Optional.of(order));
        willThrow(new BizException(ErrorCode.ORDER_ALREADY_CANCELLED))
                .given(orderDomainService).validateOrderCancellation(order);

        // When & Then
        assertThatThrownBy(() -> orderCommandService.cancelOrder(command))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_ALREADY_CANCELLED);

        then(paymentCommandUseCase).should(never()).cancelPayment(any());
    }
}
