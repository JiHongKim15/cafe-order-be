package com.cafe.order.adapter.in.web.order;

import com.cafe.order.adapter.in.web.order.mapper.OrderWebMapper;
import com.cafe.order.adapter.in.web.order.request.CancelOrderRequest;
import com.cafe.order.adapter.in.web.order.request.CreateOrderRequest;
import com.cafe.order.adapter.in.web.order.request.OrderLineRequest;
import com.cafe.order.adapter.in.web.order.response.CreateOrderResponse;
import com.cafe.order.application.port.in.order.OrderCommandUseCase;
import com.cafe.order.application.port.in.order.command.CancelOrderCommand;
import com.cafe.order.application.port.in.order.command.CreateOrderCommand;
import com.cafe.order.application.port.in.order.command.OrderLineCommand;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.order.model.Order;
import com.cafe.order.domain.order.model.OrderLine;
import com.cafe.order.domain.order.model.enums.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController API 테스트")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderCommandUseCase orderCommandUseCase;

    @MockitoBean
    private OrderWebMapper orderWebMapper;

    // ========== 주문 생성 API 테스트 ==========
    // Controller는 HTTP 요청/응답 매핑과 예외 처리만 테스트

    @Test
    @DisplayName("주문 생성 - 정상 요청 시 200 OK 응답")
    void createOrder_Success() throws Exception {
        // Given
        Long memberId = 1L;
        List<OrderLineRequest> orderLineRequests = List.of(
                new OrderLineRequest(1L, 2),
                new OrderLineRequest(2L, 1)
        );
        CreateOrderRequest request = new CreateOrderRequest(memberId, orderLineRequests);

        List<OrderLineCommand> orderLineCommands = List.of(
                new OrderLineCommand(1L, 2),
                new OrderLineCommand(2L, 1)
        );
        CreateOrderCommand command = new CreateOrderCommand(memberId, orderLineCommands);

        List<OrderLine> orderLines = List.of(
                OrderLine.builder().productId(1L).quantity(2).build(),
                OrderLine.builder().productId(2L).quantity(1).build()
        );

        Order createdOrder = Order.builder()
                .id(1L)
                .memberId(memberId)
                .orderLines(orderLines)
                .status(OrderStatus.CONFIRMED)
                .paymentId("payment-123")
                .orderDateTime(LocalDateTime.now())
                .build();

        CreateOrderResponse response = new CreateOrderResponse(
                createdOrder.getId(),
                createdOrder.getMemberId(),
                List.of(),
                createdOrder.getStatus(),
                createdOrder.getPaymentId(),
                createdOrder.getOrderDateTime()
        );

        given(orderWebMapper.toCommand(any(CreateOrderRequest.class))).willReturn(command);
        given(orderCommandUseCase.createOrder(any(CreateOrderCommand.class))).willReturn(createdOrder);
        given(orderWebMapper.toResponse(any(Order.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(1L))
                .andExpect(jsonPath("$.data.memberId").value(memberId));
    }

    @Test
    @DisplayName("주문 생성 - 비즈니스 예외 발생 시 400 에러")
    void createOrder_Fail_BizException() throws Exception {
        // Given
        Long memberId = 999L;
        List<OrderLineRequest> orderLineRequests = List.of(
                new OrderLineRequest(1L, 1)
        );
        CreateOrderRequest request = new CreateOrderRequest(memberId, orderLineRequests);

        given(orderWebMapper.toCommand(any(CreateOrderRequest.class)))
                .willReturn(new CreateOrderCommand(memberId, List.of()));
        given(orderCommandUseCase.createOrder(any(CreateOrderCommand.class)))
                .willThrow(new BizException(ErrorCode.MEMBER_NOT_FOUND));

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("M001"));
    }

    // ========== 주문 취소 API 테스트 ==========

    @Test
    @DisplayName("주문 취소 - 정상 요청 시 200 OK 응답")
    void cancelOrder_Success() throws Exception {
        // Given
        Long orderId = 1L;
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        CancelOrderCommand command = new CancelOrderCommand(orderId);

        given(orderWebMapper.toCancelCommand(any(CancelOrderRequest.class))).willReturn(command);
        willDoNothing().given(orderCommandUseCase).cancelOrder(any(CancelOrderCommand.class));

        // When & Then
        mockMvc.perform(patch("/api/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("주문 취소 - 비즈니스 예외 발생 시 400 에러")
    void cancelOrder_Fail_BizException() throws Exception {
        // Given
        Long orderId = 999L;
        CancelOrderRequest request = new CancelOrderRequest(orderId);

        given(orderWebMapper.toCancelCommand(any(CancelOrderRequest.class)))
                .willReturn(new CancelOrderCommand(orderId));
        willThrow(new BizException(ErrorCode.ORDER_NOT_FOUND))
                .given(orderCommandUseCase).cancelOrder(any(CancelOrderCommand.class));

        // When & Then
        mockMvc.perform(patch("/api/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("O001"));
    }
}
