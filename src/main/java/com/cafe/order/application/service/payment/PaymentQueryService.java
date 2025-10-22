package com.cafe.order.application.service.payment;

import com.cafe.order.application.port.in.payment.PaymentQueryUseCase;
import com.cafe.order.application.port.out.payment.PaymentPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryService implements PaymentQueryUseCase {

    private final PaymentPort paymentPort;

    @Override
    public Payment findById(Long paymentId) {
        return paymentPort.findById(paymentId)
                .orElseThrow(() -> new BizException(ErrorCode.PAYMENT_NOT_FOUND));
    }
}
