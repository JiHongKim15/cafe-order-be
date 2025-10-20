package com.cafe.order.adapter.in.web.order.mapper;

import com.cafe.order.adapter.in.web.order.request.CancelOrderRequest;
import com.cafe.order.adapter.in.web.order.request.CreateOrderRequest;
import com.cafe.order.adapter.in.web.order.response.CreateOrderResponse;
import com.cafe.order.application.port.in.order.command.CancelOrderCommand;
import com.cafe.order.application.port.in.order.command.CreateOrderCommand;
import com.cafe.order.domain.order.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderWebMapper {

    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        return new CreateOrderCommand(
            request.memberId(),
            request.productIds()
        );
    }

    public CancelOrderCommand toCancelCommand(CancelOrderRequest request) {
        return new CancelOrderCommand(request.orderId());
    }

    public CreateOrderResponse toResponse(Order order) {
        return new CreateOrderResponse(
            order.getId(),
            order.getMemberId(),
            order.getProductIds(),
            order.getStatus(),
            order.getPaymentId(),
            order.getOrderDateTime()
        );
    }
}
