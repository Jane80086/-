package com.cemenghui.meeting.controller;

import com.cemenghui.meeting.entity.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        logger.warn("参数验证失败: {} - {}", request.getDescription(false), errorMessage);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "参数验证失败: " + errorMessage));
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        logger.warn("约束违反: {} - {}", request.getDescription(false), errorMessage);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "数据验证失败: " + errorMessage));
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<String>> handleBindException(
            BindException ex, WebRequest request) {
        String errorMessage = ex.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        logger.warn("数据绑定失败: {} - {}", request.getDescription(false), errorMessage);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "数据绑定失败: " + errorMessage));
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logger.warn("非法参数: {} - {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, ex.getMessage()));
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<String>> handleSecurityException(
            SecurityException ex, WebRequest request) {
        logger.warn("权限不足: {} - {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, ex.getMessage()));
    }

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        logger.warn("资源未找到: {} - {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, ex.getMessage()));
    }

    /**
     * 处理业务逻辑异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        logger.warn("业务异常: {} - {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    /**
     * 处理数据库异常
     */
    @ExceptionHandler(java.sql.SQLException.class)
    public ResponseEntity<ApiResponse<String>> handleSQLException(
            java.sql.SQLException ex, WebRequest request) {
        logger.error("数据库异常: {} - {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "数据库操作失败，请稍后重试"));
    }

    /**
     * 处理JWT相关异常
     */
    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public ResponseEntity<ApiResponse<String>> handleJwtException(
            io.jsonwebtoken.JwtException ex, WebRequest request) {
        logger.warn("JWT异常: {} - {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "Token无效或已过期"));
    }

    /**
     * 处理其他运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        logger.error("运行时异常: {} - {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "系统内部错误，请稍后重试"));
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("未处理的异常: {} - {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "系统异常，请联系管理员"));
    }

    /**
     * 自定义资源未找到异常
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * 自定义业务异常
     */
    public static class BusinessException extends RuntimeException {
        private final int code;

        public BusinessException(String message) {
            super(message);
            this.code = 400;
        }

        public BusinessException(int code, String message) {
            super(message);
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
} 