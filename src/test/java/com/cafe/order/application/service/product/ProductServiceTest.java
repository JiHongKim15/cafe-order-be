package com.cafe.order.application.service.product;

import com.cafe.order.application.port.out.product.ProductPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.product.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 테스트")
class ProductServiceTest {

    @InjectMocks
    private ProductQueryService productService;

    @Mock
    private ProductPort productPort;

    @Test
    @DisplayName("상품 조회 성공")
    void findProductsByIds_Success() {
        // Given
        List<Long> productIds = Arrays.asList(1L, 2L);

        List<Product> products = Arrays.asList(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build(),
                Product.builder().id(2L).name("카페라떼").price(BigDecimal.valueOf(5000)).build()
        );

        given(productPort.findByIds(productIds)).willReturn(products);

        // When
        List<Product> result = productService.findProductsByIds(productIds);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("상품 조회 실패 - 일부 상품 미존재")
    void findProductsByIds_Fail_ProductNotFound() {
        // Given
        List<Long> productIds = Arrays.asList(1L, 999L);

        List<Product> foundProducts = Collections.singletonList(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build()
        );

        given(productPort.findByIds(productIds)).willReturn(foundProducts);

        // When & Then
        assertThatThrownBy(() -> productService.findProductsByIds(productIds))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }
}
