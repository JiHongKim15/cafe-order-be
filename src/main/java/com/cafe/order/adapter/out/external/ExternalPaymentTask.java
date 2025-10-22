package com.cafe.order.adapter.out.external;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalPaymentTask {

    private final PaymentApiAdapter paymentApiAdapter;

    @TimeLimiter(name = "payment")
    public CompletableFuture<String> processPaymentAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String result = paymentApiAdapter.makePayment();
                String paymentId = UUID.randomUUID().toString();
                log.info("외부 결제 API 호출 성공: paymentId={}, result={}", paymentId, result);
                return paymentId;
            } catch (Exception e) {
                throw new RuntimeException("외부 결제 API 호출 실패", e);
            }
        });
    }

    @TimeLimiter(name = "payment-cancel")
    public CompletableFuture<Void> cancelPaymentAsync(String paymentId) {
        return CompletableFuture.runAsync(() -> {
            try {
                paymentApiAdapter.cancelPayment();
                log.info("외부 결제 취소 API 호출 성공: paymentId={}", paymentId);
            } catch (Exception e) {
                throw new RuntimeException("외부 결제 취소 API 호출 실패: " + paymentId, e);
            }
        });
    }
}

