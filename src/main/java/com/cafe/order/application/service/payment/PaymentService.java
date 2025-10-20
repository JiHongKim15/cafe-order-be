package com.cafe.order.application.service.payment;

import com.cafe.order.application.port.in.payment.PaymentUseCase;
import com.cafe.order.application.port.out.payment.PaymentPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentUseCase {

    private final PaymentPort paymentPort;

    @TimeLimiter(name = "payment")
    public CompletableFuture<String> processPayment() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String paymentId = paymentPort.processPayment();
                log.info("결제 처리 성공: paymentId={}", paymentId);
                return paymentId;
            } catch (Exception e) {
                log.error("결제 처리 실패", e);
                throw new BizException(ErrorCode.PAYMENT_FAILED, "결제 처리에 실패했습니다.");
            }
        });
    }

    @TimeLimiter(name = "payment")
    public CompletableFuture<Void> cancelPayment(String paymentId) {
        return CompletableFuture.runAsync(() -> {
            try {
                paymentPort.cancelPayment(paymentId);
                log.info("결제 취소 성공: paymentId={}", paymentId);
            } catch (Exception e) {
                log.error("결제 취소 실패: paymentId={}", paymentId, e);
                throw new BizException(ErrorCode.PAYMENT_FAILED, "결제 취소에 실패했습니다.");
            }
        });
    }


    @Override
    public String processPaymentSync() {
        try {
            return processPayment().join();
        } catch (Exception e) {
            log.error("결제 처리 실패 또는 타임아웃", e);
            throw extractBizException(e, "결제 처리");
        }
    }


    @Override
    public void cancelPaymentSync(String paymentId) {
        try {
            cancelPayment(paymentId).join();
        } catch (Exception e) {
            log.error("결제 취소 실패 또는 타임아웃: paymentId={}", paymentId, e);
            throw extractBizException(e, "결제 취소");
        }
    }

    private BizException extractBizException(Exception e, String operation) {
        Throwable cause = e.getCause();
        if (cause instanceof BizException bizEx) {
            return bizEx;
        }
        return new BizException(ErrorCode.PAYMENT_FAILED, operation + "에 실패했습니다.");
    }
}

