package top.shmilyqjj.springboot.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "在线客服聊天请求")
public class CsChatReq {

    @Schema(description = "用户问题", example = "退货政策是什么？")
    private String message;
}
