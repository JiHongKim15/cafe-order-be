package com.cafe.order.adapter.out.external;

import com.cafe.order.application.port.out.payment.PaymentPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentAdapter implements PaymentPort {

    private final PaymentApiAdapter paymentApiAdapter;

    @Override
    public String processPayment() {
        try {
            String result = paymentApiAdapter.makePayment();
            String paymentId = UUID.randomUUID().toString();
            log.info("Payment processed successfully: paymentId={}, result={}", paymentId, result);
            return paymentId;
        } catch (Exception e) {
            log.error("Payment processing failed", e);
            throw new BizException(ErrorCode.INVALID_REQUEST, "결제 처리에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public String cancelPayment(String paymentId) {
        try {
            String result = paymentApiAdapter.cancelPayment();
            log.info("Payment cancelled successfully: paymentId={}, result={}", paymentId, result);
            return result;
        } catch (Exception e) {
            log.error("Payment cancellation failed: paymentId={}", paymentId, e);
            throw new BizException(ErrorCode.INVALID_REQUEST, "결제 취소에 실패했습니다: " + e.getMessage());
        }
    }
}
