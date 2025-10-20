package com.cafe.order.application.service.order;

import com.cafe.order.application.port.in.member.MemberUseCase;
import com.cafe.order.application.port.in.order.OrderUseCase;
import com.cafe.order.application.port.in.order.command.CancelOrderCommand;
import com.cafe.order.application.port.in.order.command.CreateOrderCommand;
import com.cafe.order.application.port.in.payment.PaymentUseCase;
import com.cafe.order.application.port.in.product.ProductUseCase;
import com.cafe.order.application.port.out.order.OrderPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.order.model.Order;
import com.cafe.order.domain.order.service.OrderDomainService;
import com.cafe.order.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class OrderService implements OrderUseCase {

    private final OrderPort orderPort;

    private final MemberUseCase memberUseCase;
    private final ProductUseCase productUseCase;
    private final PaymentUseCase paymentUseCase;

    private final OrderDomainService orderDomainService;

    @Override
    public Order createOrder(CreateOrderCommand command) {
        Member member = memberUseCase.findById(command.memberId());
        List<Product> products = productUseCase.findProductsByIds(command.productIds());

        orderDomainService.validateOrderCreation(member, products);

        String paymentId = paymentUseCase.processPaymentSync();

        Order order = Order.create(command.memberId(), command.productIds(), paymentId);
        Order savedOrder = orderPort.save(order);

        log.info("주문 생성 완료: orderId={}, paymentId={}", savedOrder.getId(), paymentId);
        return savedOrder;
    }


    @Override
    public void cancelOrder(CancelOrderCommand command) {
        Order order = orderPort.findById(command.orderId())
                .orElseThrow(() -> new BizException(ErrorCode.ORDER_NOT_FOUND));

        orderDomainService.validateOrderCancellation(order);

        paymentUseCase.cancelPaymentSync(order.getPaymentId());

        order.cancel();
        orderPort.save(order);

        log.info("주문 취소 완료: orderId={}, paymentId={}", order.getId(), order.getPaymentId());
    }
}
