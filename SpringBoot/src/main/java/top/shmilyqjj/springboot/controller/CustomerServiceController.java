package top.shmilyqjj.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;
import top.shmilyqjj.springboot.exception.ApiErrorCode;
import top.shmilyqjj.springboot.exception.ApiException;
import top.shmilyqjj.springboot.models.request.CsChatReq;
import top.shmilyqjj.springboot.models.response.CsChatRes;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/cs")
@RequiredArgsConstructor
@Tag(name = "在线客服", description = "基于知识库 RAG 的客服对话")
public class CustomerServiceController {

    private static final long SSE_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(5);

    @Qualifier("customerServiceChatClient")
    private final ChatClient customerServiceChatClient;

    @PostMapping("/chat")
    @Operation(summary = "客服对话", description = "根据后台配置的知识库文档检索上下文后回答")
    public CsChatRes chat(@RequestBody CsChatReq request) {
        if (request == null || !StringUtils.hasText(request.getMessage())) {
            throw new ApiException(ApiErrorCode.CS_MESSAGE_EMPTY);
        }
        log.debug("CS chat: {}", request.getMessage());
        String reply = customerServiceChatClient.prompt()
                .user(request.getMessage())
                .call()
                .content();
        return CsChatRes.builder().reply(reply).build();
    }

    /**
     * 流式输出（SSE），每条事件 data 为 JSON：{@code {"d":"增量文本"}}，便于转义换行与引号。
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "客服对话（流式）", description = "SSE，增量 JSON 字段 d；前端可拼成 Markdown 后渲染")
    public SseEmitter chatStream(@RequestBody CsChatReq request) {
        if (request == null || !StringUtils.hasText(request.getMessage())) {
            throw new ApiException(ApiErrorCode.CS_MESSAGE_EMPTY);
        }
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        Disposable subscription = customerServiceChatClient.prompt()
                .user(request.getMessage())
                .stream()
                .content()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        token -> {
                            try {
                                emitter.send(SseEmitter.event()
                                        .data(Map.of("d", token), MediaType.APPLICATION_JSON));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        err -> {
                            try {
                                emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data(Map.of(
                                                "message",
                                                err.getMessage() != null ? err.getMessage() : "stream failed"),
                                                MediaType.APPLICATION_JSON));
                            } catch (IOException ignored) {
                                // ignore
                            }
                            emitter.completeWithError(err);
                        },
                        emitter::complete
                );
        emitter.onCompletion(subscription::dispose);
        emitter.onTimeout(() -> {
            subscription.dispose();
            emitter.complete();
        });
        return emitter;
    }
}
