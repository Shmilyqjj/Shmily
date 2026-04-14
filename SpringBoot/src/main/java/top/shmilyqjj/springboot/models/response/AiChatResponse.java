package top.shmilyqjj.springboot.models.response;

import lombok.Builder;
import lombok.Data;

/**
 * AI 聊天响应
 */
@Data
@Builder
public class AiChatResponse {
    /**
     * AI 回复内容
     */
    private String content;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 本次对话 token 消耗（如果可用）
     */
    private TokenUsage tokenUsage;

    @Data
    @Builder
    public static class TokenUsage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}