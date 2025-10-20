package com.cafe.order.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 공통 에러
    INTERNAL_SERVER_ERROR("E001", "내부 서버 오류가 발생했습니다."),
    INVALID_REQUEST("E002", "잘못된 요청입니다."),
    
    // 회원 관련 에러
    MEMBER_NOT_FOUND("M001", "회원을 찾을 수 없습니다."),
    MEMBER_ALREADY_WITHDRAWN("M002", "이미 탈퇴한 회원입니다."),
    MEMBER_NOT_WITHDRAWN("M003", "탈퇴한 회원이 아닙니다."),
    WITHDRAWAL_PERIOD_NOT_EXIST("M004","탈퇴 일시 정보가 없습니다."),
    WITHDRAWAL_PERIOD_EXPIRED("M005", "탈퇴 철회 기간(30일)이 경과되었습니다."),
    
    // 주문 관련 에러
    ORDER_NOT_FOUND("O001", "주문을 찾을 수 없습니다."),
    
    // 상품 관련 에러
    PRODUCT_NOT_FOUND("P001", "상품을 찾을 수 없습니다."),
    PRODUCT_NOT_AVAILABLE("P002", "판매 중단된 상품입니다."),
    
    // 결제 관련 에러
    PAYMENT_FAILED("PAY001", "결제 처리에 실패했습니다."),
    PAYMENT_CANCELLED("PAY002", "결제가 취소되었습니다."),
    PAYMENT_TIMEOUT("PAY003", "결제 처리 시간이 초과되었습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}