package com.cafe.order.application.service.product;

import com.cafe.order.application.port.in.product.ProductUseCase;
import com.cafe.order.application.port.out.product.ProductPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 상품 애플리케이션 서비스
 *
 * Product 도메인의 진입점으로서 상품 조회 및 검증을 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService implements ProductUseCase {

    private final ProductPort productPort;

    /**
     * 상품 ID 목록으로 상품들을 조회
     *
     * 상품 존재 여부를 검증합니다.
     * 요청한 상품 중 하나라도 존재하지 않으면 예외를 발생시킵니다.
     *
     * @param productIds 조회할 상품 ID 목록
     * @return 조회된 상품 목록
     * @throws BizException 존재하지 않는 상품이 포함된 경우
     */
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
