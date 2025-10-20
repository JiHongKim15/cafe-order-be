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
            throw new BizException(ErrorCode.INVALID_REQUEST, "회원만 주문할 수 있습니다.");
        }

        if (products == null || products.isEmpty()) {
            throw new BizException(ErrorCode.INVALID_REQUEST, "주문할 상품이 없습니다.");
        }
    }

    public void validateOrderCancellation(Order order) {
        if (order.isCancelled()) {
            throw new BizException(ErrorCode.INVALID_REQUEST, "이미 취소된 주문입니다.");
        }
    }


}
