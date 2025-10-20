package com.cafe.order.adapter.in.web.common;

import com.cafe.order.common.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    
    private final boolean success;
    private final T data;
    private final String message;
    private final String errorCode;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }
    
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null, null);
    }
    
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(false, null, message, errorCode);
    }
    
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, errorCode.getMessage(), errorCode.getCode());
    }
}