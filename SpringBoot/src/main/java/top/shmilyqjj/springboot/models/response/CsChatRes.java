package top.shmilyqjj.springboot.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "在线客服聊天响应")
public class CsChatRes {
    private String reply;
}
