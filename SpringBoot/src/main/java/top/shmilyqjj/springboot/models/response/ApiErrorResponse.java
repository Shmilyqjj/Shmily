package top.shmilyqjj.springboot.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

/**
 * 统一错误响应（非 2xx 时返回）。
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    /** 是否成功，错误时恒为 false */
    boolean success;
    /** 稳定错误码，供程序判断 */
    String code;
    /** 人类可读说明 */
    String message;
    /** 请求路径（便于排查） */
    String path;
}
