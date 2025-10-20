package com.cafe.order.adapter.in.web.order;

import com.cafe.order.adapter.in.web.common.ApiResponse;
import com.cafe.order.adapter.in.web.order.mapper.OrderWebMapper;
import com.cafe.order.adapter.in.web.order.request.CancelOrderRequest;
import com.cafe.order.adapter.in.web.order.request.CreateOrderRequest;
import com.cafe.order.adapter.in.web.order.response.CreateOrderResponse;
import com.cafe.order.application.port.in.order.OrderUseCase;
import com.cafe.order.application.port.in.order.command.CancelOrderCommand;
import com.cafe.order.application.port.in.order.command.CreateOrderCommand;
import com.cafe.order.domain.order.model.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;
    private final OrderWebMapper orderWebMapper;

    @PostMapping
    public ApiResponse<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = orderWebMapper.toCommand(request);
        Order order = orderUseCase.createOrder(command);
        CreateOrderResponse response = orderWebMapper.toResponse(order);

        return ApiResponse.success(response);
    }

    @PatchMapping("/cancel")
    public ApiResponse<Void> cancelOrder(@Valid @RequestBody CancelOrderRequest request) {
        CancelOrderCommand command = orderWebMapper.toCancelCommand(request);
        orderUseCase.cancelOrder(command);
        return ApiResponse.success();
    }
}
