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
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 단위 테스트")
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductPort productPort;

    // ========== 상품 조회 시나리오 ==========

    @Test
    @DisplayName("시나리오: 여러 상품 ID로 상품 목록 조회 성공")
    void findProductsByIds_Success() {
        // Given: 조회할 상품 ID 목록
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);

        List<Product> products = Arrays.asList(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build(),
                Product.builder().id(2L).name("카페라떼").price(BigDecimal.valueOf(5000)).build(),
                Product.builder().id(3L).name("카푸치노").price(BigDecimal.valueOf(5000)).build()
        );

        given(productPort.findByIds(productIds)).willReturn(products);

        // When: 상품 조회
        List<Product> result = productService.findProductsByIds(productIds);

        // Then: 요청한 모든 상품이 조회됨
        assertThat(result).hasSize(3);
        assertThat(result).extracting("id").containsExactly(1L, 2L, 3L);
        assertThat(result).extracting("name").containsExactly("아메리카노", "카페라떼", "카푸치노");

        then(productPort).should().findByIds(productIds);
    }

    @Test
    @DisplayName("시나리오: 고객이 아메리카노 1잔만 주문")
    void findProductsByIds_Success_SingleProduct() {
        // Given: 단일 상품 ID
        List<Long> productIds = Collections.singletonList(1L);

        List<Product> products = Collections.singletonList(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build()
        );

        given(productPort.findByIds(productIds)).willReturn(products);

        // When: 상품 조회
        List<Product> result = productService.findProductsByIds(productIds);

        // Then: 상품 1개 조회 성공
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("아메리카노");
        assertThat(result.get(0).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(4500));

        then(productPort).should().findByIds(productIds);
    }

    @Test
    @DisplayName("시나리오: 존재하지 않는 상품 ID가 포함되면 실패")
    void findProductsByIds_Fail_ProductNotFound() {
        // Given: 3개 요청했지만 2개만 존재
        List<Long> productIds = Arrays.asList(1L, 2L, 999L);

        List<Product> foundProducts = Arrays.asList(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build(),
                Product.builder().id(2L).name("카페라떼").price(BigDecimal.valueOf(5000)).build()
                // ID 999는 존재하지 않음
        );

        given(productPort.findByIds(productIds)).willReturn(foundProducts);

        // When & Then: 상품 수 불일치로 예외 발생
        assertThatThrownBy(() -> productService.findProductsByIds(productIds))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

        then(productPort).should().findByIds(productIds);
    }

    @Test
    @DisplayName("시나리오: 모든 상품 ID가 존재하지 않으면 실패")
    void findProductsByIds_Fail_AllProductsNotFound() {
        // Given: 존재하지 않는 상품 ID만 요청
        List<Long> productIds = Arrays.asList(998L, 999L);

        given(productPort.findByIds(productIds)).willReturn(Collections.emptyList());

        // When & Then: 조회된 상품이 없음
        assertThatThrownBy(() -> productService.findProductsByIds(productIds))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

        then(productPort).should().findByIds(productIds);
    }

    @Test
    @DisplayName("시나리오: 같은 상품을 여러 개 주문 (중복 ID)")
    void findProductsByIds_Success_DuplicateIds() {
        // Given: 아메리카노 2잔 주문 (ID 중복)
        List<Long> productIds = Arrays.asList(1L, 1L);

        List<Product> products = Arrays.asList(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build(),
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build()
        );

        given(productPort.findByIds(productIds)).willReturn(products);

        // When: 상품 조회
        List<Product> result = productService.findProductsByIds(productIds);

        // Then: 중복된 상품도 정상 조회
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").containsExactly(1L, 1L);

        then(productPort).should().findByIds(productIds);
    }

    @Test
    @DisplayName("시나리오: 대량 주문 - 8가지 메뉴 전체 주문")
    void findProductsByIds_Success_BulkOrder() {
        // Given: 전체 메뉴 주문
        List<Long> productIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);

        List<Product> products = Arrays.asList(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build(),
                Product.builder().id(2L).name("카페라떼").price(BigDecimal.valueOf(5000)).build(),
                Product.builder().id(3L).name("카푸치노").price(BigDecimal.valueOf(5000)).build(),
                Product.builder().id(4L).name("바닐라라떼").price(BigDecimal.valueOf(5500)).build(),
                Product.builder().id(5L).name("카라멜마키아또").price(BigDecimal.valueOf(6000)).build(),
                Product.builder().id(6L).name("에스프레소").price(BigDecimal.valueOf(4000)).build(),
                Product.builder().id(7L).name("핫초코").price(BigDecimal.valueOf(5500)).build(),
                Product.builder().id(8L).name("녹차라떼").price(BigDecimal.valueOf(5500)).build()
        );

        given(productPort.findByIds(productIds)).willReturn(products);

        // When: 상품 조회
        List<Product> result = productService.findProductsByIds(productIds);

        // Then: 전체 메뉴 조회 성공
        assertThat(result).hasSize(8);
        assertThat(result).extracting("name").contains(
                "아메리카노", "카페라떼", "카푸치노", "바닐라라떼",
                "카라멜마키아또", "에스프레소", "핫초코", "녹차라떼"
        );

        then(productPort).should().findByIds(productIds);
    }

    @Test
    @DisplayName("시나리오: 일부 상품만 재고 소진되어 누락된 경우")
    void findProductsByIds_Fail_PartialStockOut() {
        // Given: 3개 주문했지만 1개는 재고 없음
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);

        List<Product> foundProducts = Arrays.asList(
                Product.builder().id(1L).name("아메리카노").price(BigDecimal.valueOf(4500)).build(),
                Product.builder().id(3L).name("카푸치노").price(BigDecimal.valueOf(5000)).build()
                // ID 2 (카페라떼)는 재고 소진으로 조회 안 됨
        );

        given(productPort.findByIds(productIds)).willReturn(foundProducts);

        // When & Then: 일부 상품 누락으로 예외 발생
        assertThatThrownBy(() -> productService.findProductsByIds(productIds))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

        then(productPort).should().findByIds(productIds);
    }
}
