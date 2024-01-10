package top.shmilyqjj.springboot.controller.channel;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import top.shmilyqjj.springboot.services.UserService;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 这是一个websocket的Demo示例 只支持单个长连接 不支持多连接
 */
@Component
@ServerEndpoint(value = "/ws/demo")
@Tag(name = "websocket", description = "websocket接口")
public class WebSocketDemoChannel implements ApplicationContextAware {
    // WebSocket 是一种基于 TCP 协议的全双工通信协议，它允许客户端和服务器之间建立持久的、双向的通信连接。相比传统的 HTTP 请求 - 响应模式，WebSocket 提供了实时、低延迟的数据传输能力。通过 WebSocket，客户端和服务器可以在任意时间点互相发送消息，实现实时更新和即时通信的功能。WebSocket 协议经过了多个浏览器和服务器的支持，成为了现代 Web 应用中常用的通信协议之一。它广泛应用于聊天应用、实时数据更新、多人游戏等场景，为 Web 应用提供了更好的用户体验和更高效的数据传输方式。
    private static final Logger logger = LoggerFactory.getLogger(WebSocketDemoChannel.class);
    private Session session;
    private static ApplicationContext applicationContext;
    private UserService userService;

    // 收到消息
    @OnMessage
    public void onMessage(String message, boolean last) throws IOException {

        logger.info("[websocket] 收到消息：id={}，message={} 是否是最后一条消息：{}", this.session.getId(), message, last);

        if ("bye".equalsIgnoreCase(message)) {
            // 由服务器主动关闭连接。状态码为 NORMAL_CLOSURE（正常关闭）。
            this.session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Bye"));;
            return;
        }

        if ("query_user".equalsIgnoreCase(message)) {
            this.session.getAsyncRemote().sendText(userService.getUserList().toString());
            return;
        }

        this.session.getAsyncRemote().sendText("["+ Instant.now().toEpochMilli() +"] Hello " + message);
    }

    // 连接打开
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig){
        // 保存 session 到对象
        this.session = session;
        Map<String, List<String>> query = session.getRequestParameterMap();
        // 手动获取注入的对象
        this.userService = WebSocketDemoChannel.applicationContext.getBean(UserService.class);
        logger.info("[websocket] 新的连接：id={} 数据:{}", this.session.getId(), query.toString());
    }

    // 连接关闭
    @OnClose
    public void onClose(CloseReason closeReason){
        logger.info("[websocket] 连接断开：id={}，reason={}", this.session.getId(),closeReason);
    }

    // 连接异常
    @OnError
    public void onError(Throwable throwable) throws IOException {

        logger.info("[websocket] 连接异常：id={}，throwable={}", this.session.getId(), throwable.getMessage());

        // 关闭连接。状态码为 UNEXPECTED_CONDITION（意料之外的异常）
        this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        //WebSocket接口 无法使用Autowired注入 需要手动从Spring对象池获取对象  通过WebSocketChannel.applicationContext.getBean(XXX.class)获取
        WebSocketDemoChannel.applicationContext = applicationContext;
    }
}
