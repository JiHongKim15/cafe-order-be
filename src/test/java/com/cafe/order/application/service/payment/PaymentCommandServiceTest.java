package com.cafe.order.application.service.payment;

import com.cafe.order.application.port.in.payment.command.CancelPaymentCommand;
import com.cafe.order.application.port.in.payment.command.ProcessPaymentCommand;
import com.cafe.order.application.port.out.payment.ExternalPaymentPort;
import com.cafe.order.application.port.out.payment.PaymentPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.payment.model.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentCommandService 테스트")
class PaymentCommandServiceTest {

    @InjectMocks
    private PaymentCommandService paymentCommandService;

    @Mock
    private PaymentPort paymentPort;

    @Mock
    private ExternalPaymentPort externalPaymentPort;

    // ========== 결제 처리 ==========

    @Test
    @DisplayName("결제 처리 성공")
    void processPayment_Success() {
        // Given
        Long memberId = 1L;
        ProcessPaymentCommand command = new ProcessPaymentCommand(memberId);

        String externalPaymentId = "ext-payment-123";
        Payment savedPayment = Payment.builder()
                .id(1L)
                .paymentId(externalPaymentId)
                .orderId(null)
                .paymentDateTime(LocalDateTime.now())
                .build();

        given(externalPaymentPort.processPayment()).willReturn(externalPaymentId);
        given(paymentPort.save(any(Payment.class))).willReturn(savedPayment);

        // When
        Payment result = paymentCommandService.processPayment(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPaymentId()).isEqualTo(externalPaymentId);
    }

    @Test
    @DisplayName("결제 처리 실패 - 외부 API 오류")
    void processPayment_Fail_ExternalApiError() {
        // Given
        Long memberId = 1L;
        ProcessPaymentCommand command = new ProcessPaymentCommand(memberId);

        given(externalPaymentPort.processPayment())
                .willThrow(new BizException(ErrorCode.PAYMENT_FAILED));

        // When & Then
        assertThatThrownBy(() -> paymentCommandService.processPayment(command))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_FAILED);
    }

    // ========== 결제 취소 ==========

    @Test
    @DisplayName("결제 취소 성공")
    void cancelPayment_Success() {
        // Given
        String paymentId = "ext-payment-123";
        CancelPaymentCommand command = new CancelPaymentCommand(paymentId);

        willDoNothing().given(externalPaymentPort).cancelPayment(paymentId);

        // When
        paymentCommandService.cancelPayment(command);

        // Then
        then(externalPaymentPort).should().cancelPayment(paymentId);
    }

    @Test
    @DisplayName("결제 취소 실패 - 외부 API 오류")
    void cancelPayment_Fail_ExternalApiError() {
        // Given
        String paymentId = "ext-payment-123";
        CancelPaymentCommand command = new CancelPaymentCommand(paymentId);

        willThrow(new BizException(ErrorCode.PAYMENT_FAILED))
                .given(externalPaymentPort).cancelPayment(paymentId);

        // When & Then
        assertThatThrownBy(() -> paymentCommandService.cancelPayment(command))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_FAILED);
    }
}
