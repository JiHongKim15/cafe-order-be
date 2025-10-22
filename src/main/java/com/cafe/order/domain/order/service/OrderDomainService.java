package com.cafe.order.domain.order.service;

import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.order.model.Order;
import com.cafe.order.domain.product.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderDomainService {

    public void validateOrderCreation(Member member, List<Product> products) {
        if (!member.isActive()) {
            throw new BizException(ErrorCode.ORDER_MEMBER_NOT_ACTIVE);
        }

        if (products == null || products.isEmpty()) {
            throw new BizException(ErrorCode.ORDER_EMPTY_PRODUCTS);
        }
    }

    public void validateOrderCancellation(Order order) {
        if (order.isCancelled()) {
            throw new BizException(ErrorCode.ORDER_ALREADY_CANCELLED);
        }
    }


}
