package com.cafe.order.application.service.order;

import com.cafe.order.application.port.in.member.MemberQueryUseCase;
import com.cafe.order.application.port.in.order.OrderCommandUseCase;
import com.cafe.order.application.port.in.order.command.CancelOrderCommand;
import com.cafe.order.application.port.in.order.command.CreateOrderCommand;
import com.cafe.order.application.port.in.order.command.OrderLineCommand;
import com.cafe.order.application.port.in.payment.PaymentCommandUseCase;
import com.cafe.order.application.port.in.payment.command.CancelPaymentCommand;
import com.cafe.order.application.port.in.payment.command.ProcessPaymentCommand;
import com.cafe.order.domain.payment.model.Payment;
import com.cafe.order.application.port.in.product.ProductQueryUseCase;
import com.cafe.order.application.port.out.order.OrderPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.order.model.Order;
import com.cafe.order.domain.order.model.OrderLine;
import com.cafe.order.domain.order.service.OrderDomainService;
import com.cafe.order.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class OrderCommandCommandService implements OrderCommandUseCase {

    private final OrderPort orderPort;

    private final MemberQueryUseCase memberQueryUseCase;
    private final ProductQueryUseCase productQueryUseCase;
    private final PaymentCommandUseCase paymentCommandUseCase;

    private final OrderDomainService orderDomainService;

    @Override
    public Order createOrder(CreateOrderCommand command) {
        Member member = memberQueryUseCase.findById(command.memberId());

        List<Long> productIds = command.orderLines().stream()
                .map(OrderLineCommand::productId)
                .distinct()
                .collect(Collectors.toList());
        List<Product> products = productQueryUseCase.findProductsByIds(productIds);

        orderDomainService.validateOrderCreation(member, products);

        List<OrderLine> orderLines = command.orderLines().stream()
                .map(orderLineCommand -> OrderLine.of(orderLineCommand.productId(), orderLineCommand.quantity()))
                .collect(Collectors.toList());

        Payment payment = paymentCommandUseCase.processPayment(new ProcessPaymentCommand(command.memberId()));

        Order order = Order.create(command.memberId(), orderLines, payment.getPaymentId());
        Order savedOrder = orderPort.save(order);

        log.info("주문 생성 완료: orderId={}, paymentId={}", savedOrder.getId(), payment.getPaymentId());
        return savedOrder;
    }


    @Override
    public void cancelOrder(CancelOrderCommand command) {
        Order order = orderPort.findById(command.orderId())
                .orElseThrow(() -> new BizException(ErrorCode.ORDER_NOT_FOUND));

        orderDomainService.validateOrderCancellation(order);

        CancelPaymentCommand cancelPaymentCommand = new CancelPaymentCommand(order.getPaymentId());
        paymentCommandUseCase.cancelPayment(cancelPaymentCommand);

        order.cancel();
        orderPort.save(order);

        log.info("주문 취소 완료: orderId={}, paymentId={}", order.getId(), order.getPaymentId());
    }
}
