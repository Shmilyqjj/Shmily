package top.shmilyqjj.springboot.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.client.RestClientException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import top.shmilyqjj.springboot.exception.ApiErrorCode;
import top.shmilyqjj.springboot.exception.ApiException;
import top.shmilyqjj.springboot.models.response.ApiErrorResponse;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        return body(ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.debug("IllegalArgument: {}", ex.getMessage());
        return body(ApiErrorCode.ILLEGAL_ARGUMENT, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.debug("Unreadable JSON: {}", ex.getMessage());
        return body(ApiErrorCode.INVALID_JSON, ApiErrorCode.INVALID_JSON.getDefaultMessage(), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String msg = "缺少参数: " + ex.getParameterName();
        return body(ApiErrorCode.MISSING_REQUEST_PARAMETER, msg, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String msg = "参数「" + ex.getName() + "」类型不正确";
        return body(ApiErrorCode.TYPE_MISMATCH, msg, request);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiErrorResponse> handleNumberFormat(NumberFormatException ex, HttpServletRequest request) {
        return body(ApiErrorCode.ILLEGAL_ARGUMENT, "数字格式不正确", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(m -> m != null && !m.isBlank())
                .findFirst()
                .orElseGet(() -> ex.getBindingResult().getFieldErrors().stream()
                        .map(fe -> fe.getField() + " 无效")
                        .collect(Collectors.joining("; ")));
        if (msg.isBlank()) {
            msg = ApiErrorCode.REQUEST_VALIDATION_FAILED.getDefaultMessage();
        }
        return body(ApiErrorCode.REQUEST_VALIDATION_FAILED, msg, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String supported = ex.getSupportedHttpMethods() != null ? ex.getSupportedHttpMethods().toString() : "";
        String msg = supported.isEmpty()
                ? ApiErrorCode.HTTP_METHOD_NOT_SUPPORTED.getDefaultMessage()
                : ApiErrorCode.HTTP_METHOD_NOT_SUPPORTED.getDefaultMessage() + "，允许: " + supported;
        return body(ApiErrorCode.HTTP_METHOD_NOT_SUPPORTED, msg, request);
    }

    @ExceptionHandler(JedisConnectionException.class)
    public ResponseEntity<ApiErrorResponse> handleJedisConnection(JedisConnectionException ex, HttpServletRequest request) {
        log.warn("Redis/Jedis connection error: {}", ex.getMessage());
        Throwable root = ex.getCause() != null ? ex.getCause() : ex;
        String detail = root.getMessage() != null ? root.getMessage() : ApiErrorCode.AI_UPSTREAM_FAILED.getDefaultMessage();
        if (detail.length() > 240) {
            detail = detail.substring(0, 240) + "…";
        }
        return body(ApiErrorCode.AI_UPSTREAM_FAILED, detail, request);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiErrorResponse> handleRestClient(RestClientException ex, HttpServletRequest request) {
        log.warn("Upstream HTTP client error: {}", ex.getMessage());
        Throwable root = ex.getRootCause() != null ? ex.getRootCause() : ex;
        String detail = root.getMessage() != null ? root.getMessage() : ApiErrorCode.AI_UPSTREAM_FAILED.getDefaultMessage();
        if (detail.length() > 240) {
            detail = detail.substring(0, 240) + "…";
        }
        return body(ApiErrorCode.AI_UPSTREAM_FAILED, detail, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAny(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        return body(ApiErrorCode.INTERNAL_ERROR, ApiErrorCode.INTERNAL_ERROR.getDefaultMessage(), request);
    }

    private static ResponseEntity<ApiErrorResponse> body(ApiErrorCode code, String message, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .success(false)
                .code(code.getCode())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }
}
