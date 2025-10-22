package com.cafe.order.adapter.out.external;

import com.cafe.order.application.port.out.payment.ExternalPaymentPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalPaymentAdapter implements ExternalPaymentPort {

    private final ExternalPaymentTask externalPaymentTask;

    @Override
    public String processPayment() {
        try {
            return externalPaymentTask.processPaymentAsync().get();
        } catch (Exception e) {
            log.error("외부 결제 API 호출 실패", e);
            throw new BizException(ErrorCode.PAYMENT_FAILED);
        }
    }

    @Override
    public void cancelPayment(String paymentId) {
        try {
            externalPaymentTask.cancelPaymentAsync(paymentId).get();
        } catch (Exception e) {
            log.error("외부 결제 취소 API 호출 실패: paymentId={}", paymentId, e);
            throw new BizException(ErrorCode.PAYMENT_FAILED);
        }
    }
}
