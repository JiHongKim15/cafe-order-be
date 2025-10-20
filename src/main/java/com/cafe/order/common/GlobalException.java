package com.cafe.order.common;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    private final String errorCode;

    public GlobalException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GlobalException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}