package com.cafe.order.adapter.out.external;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 외부 결제 API 클라이언트
 * 실제 외부 결제 시스템을 호출한다고 가정 (Mock)
 */
@Component
public class PaymentApiAdapter {

    public String makePayment() throws Exception {
        Thread.sleep((long) (Math.random() * 1000));
        Random random = new Random();
        if (random.nextInt() % 100 == 1) {
            throw new Exception("Payment failed!");
        }
        return "Success!";
    }

    public String cancelPayment() throws Exception {
        Thread.sleep((long) (Math.random() * 1000));
        Random random = new Random();
        if (random.nextInt() % 100 == 1) {
            throw new Exception("Payment cancellation failed!");
        }
        return "Success!";
    }
}