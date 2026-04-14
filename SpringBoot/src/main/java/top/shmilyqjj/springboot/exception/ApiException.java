package top.shmilyqjj.springboot.exception;

import lombok.Getter;

/**
 * 业务异常：携带统一 {@link ApiErrorCode}，由全局处理器转为 JSON。
 */
@Getter
public class ApiException extends RuntimeException {

    private final ApiErrorCode errorCode;

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public ApiException(ApiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(ApiErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
