package com.cafe.order.adapter.in.web.order.mapper;

import com.cafe.order.adapter.in.web.order.request.CancelOrderRequest;
import com.cafe.order.adapter.in.web.order.request.CreateOrderRequest;
import com.cafe.order.adapter.in.web.order.response.CreateOrderResponse;
import com.cafe.order.adapter.in.web.order.response.OrderLineResponse;
import com.cafe.order.application.port.in.order.command.CancelOrderCommand;
import com.cafe.order.application.port.in.order.command.CreateOrderCommand;
import com.cafe.order.application.port.in.order.command.OrderLineCommand;
import com.cafe.order.domain.order.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderWebMapper {

    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        List<OrderLineCommand> orderLineCommands = request.orderLines().stream()
                .map(orderLineRequest -> new OrderLineCommand(
                        orderLineRequest.productId(),
                        orderLineRequest.quantity()
                ))
                .collect(Collectors.toList());

        return new CreateOrderCommand(
            request.memberId(),
            orderLineCommands
        );
    }

    public CancelOrderCommand toCancelCommand(CancelOrderRequest request) {
        return new CancelOrderCommand(request.orderId());
    }

    public CreateOrderResponse toResponse(Order order) {
        List<OrderLineResponse> orderLineResponses = order.getOrderLines().stream()
                .map(orderLine -> new OrderLineResponse(
                        orderLine.getProductId(),
                        orderLine.getQuantity()
                ))
                .collect(Collectors.toList());

        return new CreateOrderResponse(
            order.getId(),
            order.getMemberId(),
            orderLineResponses,
            order.getStatus(),
            order.getPaymentId(),
            order.getOrderDateTime()
        );
    }
}
