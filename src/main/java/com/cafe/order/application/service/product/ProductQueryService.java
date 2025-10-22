package com.cafe.order.application.service.product;

import com.cafe.order.application.port.in.product.ProductQueryUseCase;
import com.cafe.order.application.port.out.product.ProductPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService implements ProductQueryUseCase {

    private final ProductPort productPort;

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        List<Product> products = productPort.findByIds(productIds);

        if (products.size() != productIds.size()) {
            log.error("상품 조회 실패: 요청={}, 조회={}", productIds.size(), products.size());
            throw new BizException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return products;
    }
}
