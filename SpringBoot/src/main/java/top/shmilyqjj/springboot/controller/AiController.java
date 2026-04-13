package top.shmilyqjj.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.shmilyqjj.springboot.models.request.AiChatReq;
import top.shmilyqjj.springboot.models.response.AiChatResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 聊天接口 Demo
 * 使用 Spring AI 调用大模型服务
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Chat", description = "AI 聊天接口 - 调用大模型服务")
public class AiController {

    private final ChatModel chatModel;

    @Value("${spring.ai.openai.base-url:未配置}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key:未配置}")
    private String apiKey;

    @Value("${spring.ai.openai.chat.options.model:未配置}")
    private String model;

    @GetMapping("/config")
    @Operation(summary = "查看AI配置", description = "诊断接口：查看当前Spring AI配置")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("baseUrl", baseUrl);
        config.put("apiKey", apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : apiKey);
        config.put("model", model);
        config.put("chatModelInjected", chatModel != null ? "YES" : "NO");
        return config;
    }

    @PostMapping("/chat")
    @Operation(summary = "AI 聊天", description = "发送消息给 AI 模型并获取回复")
    public AiChatResponse chat(@RequestBody AiChatReq request) {
        log.info("AI chat request: message={}, model={}", request.getMessage(), request.getModel());

        // 构建聊天选项（支持请求级别的参数覆盖）
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(request.getModel() != null ? request.getModel() : model)
                .temperature(request.getTemperature() != null ? request.getTemperature() : 0.7)
                .build();

        // 使用 UserMessage 确保正确的 role 格式
        UserMessage userMessage = new UserMessage(request.getMessage());
        Prompt prompt = new Prompt(List.of(userMessage), options);

        // 发送聊天请求
        ChatResponse response = chatModel.call(prompt);

        // 构建响应
        String content = response.getResult().getOutput().getText();
        String usedModel = response.getMetadata().getModel();

        AiChatResponse.TokenUsage tokenUsage = null;
        if (response.getMetadata().getUsage() != null) {
            tokenUsage = AiChatResponse.TokenUsage.builder()
                    .promptTokens(response.getMetadata().getUsage().getPromptTokens())
                    .completionTokens(response.getMetadata().getUsage().getCompletionTokens())
                    .totalTokens(response.getMetadata().getUsage().getTotalTokens())
                    .build();
        }

        log.info("AI chat response: model={}, tokens={}", usedModel, tokenUsage);

        return AiChatResponse.builder()
                .content(content)
                .model(usedModel)
                .tokenUsage(tokenUsage)
                .build();
    }

    @PostMapping("/chat/simple")
    @Operation(summary = "简单聊天", description = "发送消息给 AI 模型，直接返回文本回复（请求体为纯文本，建议 Content-Type: text/plain; charset=UTF-8）")
    public String simpleChat(HttpServletRequest request) throws IOException {
        Charset charset = resolveRequestCharset(request);
        String message = StreamUtils.copyToString(request.getInputStream(), charset);
        log.info("Simple AI chat request: {}", message);
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(userMessage));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    private static Charset resolveRequestCharset(HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();
        if (encoding != null && !encoding.isEmpty()) {
            try {
                return Charset.forName(encoding);
            } catch (IllegalArgumentException ignored) {
                // fall through
            }
        }
        return StandardCharsets.UTF_8;
    }
}