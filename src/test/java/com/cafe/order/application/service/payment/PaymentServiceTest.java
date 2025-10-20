package com.cafe.order.application.service.payment;

import com.cafe.order.application.port.out.payment.PaymentPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 단위 테스트")
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentPort paymentPort;

    @Test
    @DisplayName("결제 처리 성공")
    void processPayment_Success() {
        // Given
        String expectedPaymentId = "payment-123";
        given(paymentPort.processPayment()).willReturn(expectedPaymentId);

        // When
        CompletableFuture<String> result = paymentService.processPayment();

        // Then
        assertThat(result.join()).isEqualTo(expectedPaymentId);
        then(paymentPort).should().processPayment();
    }

    @Test
    @DisplayName("결제 처리 실패 - 외부 API 에러")
    void processPayment_Fail_ExternalApiError() {
        // Given
        given(paymentPort.processPayment())
                .willThrow(new RuntimeException("External API error"));

        // When
        CompletableFuture<String> result = paymentService.processPayment();

        // Then
        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .cause()
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_FAILED)
                .hasMessageContaining("결제 처리");

        then(paymentPort).should().processPayment();
    }

    @Test
    @DisplayName("결제 취소 성공")
    void cancelPayment_Success() {
        // Given
        String paymentId = "payment-123";

        // When
        CompletableFuture<Void> result = paymentService.cancelPayment(paymentId);

        // Then
        result.join(); // 완료 대기
        then(paymentPort).should().cancelPayment(paymentId);
    }

    @Test
    @DisplayName("결제 취소 실패 - 외부 API 에러")
    void cancelPayment_Fail_ExternalApiError() {
        // Given
        String paymentId = "payment-123";
        given(paymentPort.cancelPayment(paymentId))
                .willThrow(new RuntimeException("External API error"));

        // When
        CompletableFuture<Void> result = paymentService.cancelPayment(paymentId);

        // Then
        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .cause()
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_FAILED)
                .hasMessage("결제 취소에 실패했습니다.");

        then(paymentPort).should().cancelPayment(paymentId);
    }
}
