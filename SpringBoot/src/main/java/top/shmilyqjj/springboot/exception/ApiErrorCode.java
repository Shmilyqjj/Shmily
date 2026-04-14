package top.shmilyqjj.springboot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 对外错误码：与 HTTP 状态对应，便于客户端分支处理。
 */
@Getter
public enum ApiErrorCode {
    KB_DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "KB_DOCUMENT_NOT_FOUND", "文档不存在"),
    KB_FILE_EMPTY(HttpStatus.BAD_REQUEST, "KB_FILE_EMPTY", "文件不能为空"),
    KB_MISSING_ORIGINAL_FILENAME(HttpStatus.BAD_REQUEST, "KB_MISSING_ORIGINAL_FILENAME", "缺少原始文件名"),
    KB_UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "KB_UNSUPPORTED_FILE_TYPE", "不支持的文件类型"),

    CS_MESSAGE_EMPTY(HttpStatus.BAD_REQUEST, "CS_MESSAGE_EMPTY", "message 不能为空"),

    AI_MESSAGE_EMPTY(HttpStatus.BAD_REQUEST, "AI_MESSAGE_EMPTY", "message 不能为空"),

    INVALID_JSON(HttpStatus.BAD_REQUEST, "INVALID_JSON", "请求体 JSON 格式无效或无法解析"),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "MISSING_REQUEST_PARAMETER", "缺少必填请求参数"),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", "参数类型不匹配"),
    REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "REQUEST_VALIDATION_FAILED", "请求参数校验未通过"),
    HTTP_METHOD_NOT_SUPPORTED(HttpStatus.METHOD_NOT_ALLOWED, "HTTP_METHOD_NOT_SUPPORTED", "HTTP 方法不支持"),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, "ILLEGAL_ARGUMENT", "请求参数不合法"),

    AI_UPSTREAM_FAILED(HttpStatus.BAD_GATEWAY, "AI_UPSTREAM_FAILED", "调用大模型或向量服务失败"),

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "服务器内部错误");

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    ApiErrorCode(HttpStatus httpStatus, String code, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
