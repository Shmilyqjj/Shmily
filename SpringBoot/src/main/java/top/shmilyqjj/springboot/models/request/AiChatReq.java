package top.shmilyqjj.springboot.models.request;

import lombok.Data;

/**
 * AI 聊天请求
 */
@Data
public class AiChatReq {
    /**
     * 用户消息内容
     */
    private String message;

    /**
     * 可选：指定模型（覆盖默认配置）
     */
    private String model;

    /**
     * 可选：温度参数（0-2）
     */
    private Double temperature;
}