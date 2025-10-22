package com.cafe.order.application.port.in.order;

import com.cafe.order.application.port.in.order.command.CancelOrderCommand;
import com.cafe.order.application.port.in.order.command.CreateOrderCommand;
import com.cafe.order.domain.order.model.Order;
import jakarta.validation.Valid;

public interface OrderCommandUseCase {

    Order createOrder(@Valid CreateOrderCommand command);

    void cancelOrder(@Valid CancelOrderCommand command);
}
