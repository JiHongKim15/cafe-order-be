package com.cafe.order.common;

import com.cafe.order.adapter.in.web.common.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Void>> handleBizException(BizException e) {
        log.warn("BizException occurred: code={}, message={}", e.getErrorCode().getCode(), e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(e.getErrorCode().getCode(), e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(GlobalException e) {
        log.error("GlobalException occurred: code={}, message={}", e.getErrorCode(), e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation error occurred: {}", e.getMessage());

        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("입력값이 올바르지 않습니다.");

        ApiResponse<Void> response = ApiResponse.error("VALIDATION_ERROR", message);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Constraint violation occurred: {}", e.getMessage());

        String message = e.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("입력값이 올바르지 않습니다.");

        ApiResponse<Void> response = ApiResponse.error("VALIDATION_ERROR", message);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException occurred: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.error("INVALID_ARGUMENT", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected error occurred", e);
        
        ApiResponse<Void> response = ApiResponse.error("INTERNAL_SERVER_ERROR", "내부 서버 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}