package com.cafe.order.config;

import com.cafe.order.adapter.out.persistence.product.ProductJpaEntity;
import com.cafe.order.adapter.out.persistence.product.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public void run(String... args) {
        // 이미 데이터가 있으면 초기화하지 않음
        if (productJpaRepository.count() > 0) {
            log.info("상품 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("상품 초기 데이터를 INSERT합니다...");

        productJpaRepository.save(ProductJpaEntity.builder()
                .name("아메리카노")
                .price(BigDecimal.valueOf(4500))
                .build());

        productJpaRepository.save(ProductJpaEntity.builder()
                .name("카페라떼")
                .price(BigDecimal.valueOf(5000))
                .build());

        productJpaRepository.save(ProductJpaEntity.builder()
                .name("카푸치노")
                .price(BigDecimal.valueOf(5000))
                .build());

        productJpaRepository.save(ProductJpaEntity.builder()
                .name("바닐라라떼")
                .price(BigDecimal.valueOf(5500))
                .build());

        productJpaRepository.save(ProductJpaEntity.builder()
                .name("카라멜마키아또")
                .price(BigDecimal.valueOf(6000))
                .build());

        productJpaRepository.save(ProductJpaEntity.builder()
                .name("에스프레소")
                .price(BigDecimal.valueOf(4000))
                .build());

        productJpaRepository.save(ProductJpaEntity.builder()
                .name("핫초코")
                .price(BigDecimal.valueOf(5500))
                .build());

        productJpaRepository.save(ProductJpaEntity.builder()
                .name("녹차라떼")
                .price(BigDecimal.valueOf(5500))
                .build());

        log.info("상품 초기 데이터 INSERT 완료: 총 {} 개", productJpaRepository.count());
    }
}
