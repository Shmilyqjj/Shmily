package top.shmilyqjj.springboot.controller.channel;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import top.shmilyqjj.springboot.models.entity.WebSocketConnection;
import top.shmilyqjj.springboot.services.UserService;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Shmily
 * @Description: 支持多实例长连接的websocket服务 支持定时心跳检测
 * @CreateTime: 2024/1/10 下午2:48
 */

@Component
@ServerEndpoint(value = "/ws/query/{clientId}")
@Tag(name = "websocket", description = "websocket接口")
public class WebsocketChannel implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketChannel.class);
    private static ApplicationContext applicationContext;
    private static final Gson gson = new Gson();
    private static final ConcurrentHashMap<String, WebSocketConnection> websocketClients = new ConcurrentHashMap<>();

    private static final String heartbeatStr = "[\"heartbeat\", \"\"]";
    private Heartbeat heartbeat;
    private static final long heartbeatTimeoutMs = 60 * 1000L;

    private UserService userService;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext appContext) throws BeansException {
        WebsocketChannel.applicationContext = appContext;
    }

    // 连接打开
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig, @PathParam("clientId") String clientId){
        Map<String, List<String>> query = session.getRequestParameterMap();
        logger.info("[websocket clientId: {} SessionId: {}]NewConn query: {}", clientId, session.getId(), query.toString());
        WebSocketConnection conn = new WebSocketConnection(clientId, session, System.currentTimeMillis(), heartbeat);
        websocketClients.put(clientId, conn);
        this.heartbeat = new Heartbeat(conn);
        this.heartbeat.start();
        this.userService = WebsocketChannel.applicationContext.getBean(UserService.class);
    }

    // 收到消息
    @OnMessage
    public void onMessage(String message, boolean last, @PathParam("clientId") String clientId) throws IOException {
        if (websocketClients.containsKey(clientId)) {
            WebSocketConnection conn = websocketClients.get(clientId);
            conn.setLastHeartbeatTime(System.currentTimeMillis());
            Session session = conn.getSession();

            logger.info("[websocket] 收到消息：id={}，message={} 是否是最后一条消息：{}", session.getId(), message, last);

            if ("bye".equalsIgnoreCase(message)) {
                // 由服务器主动关闭连接。状态码为 NORMAL_CLOSURE（正常关闭）。
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Bye"));;
                return;
            }

            if ("query_user".equalsIgnoreCase(message)) {
                session.getAsyncRemote().sendText(userService.getUserList().toString());
                return;
            }

            session.getAsyncRemote().sendText("["+ Instant.now().toEpochMilli() +"] Hello " + message);

        }else {
            logger.warn("clientId {} is not an active websocket connection", clientId);
        }
    }



    // 连接关闭
    @OnClose
    public void onClose(CloseReason closeReason, @PathParam("clientId") String clientId){
        if (websocketClients.containsKey(clientId)) {
            WebSocketConnection conn = websocketClients.get(clientId);
            conn.getHeartbeat().shutdown();
            websocketClients.remove(clientId);
            logger.info("[websocket clientId: {} SessionId: {}]]ConnClose reason: {}", clientId, conn.getSession().getId(),closeReason);
        }else {
            logger.warn("clientId {} is not an active websocket connection", clientId);
        }
    }

    // 连接异常
    @OnError
    public void onError(Throwable throwable, @PathParam("clientId") String clientId) throws IOException {
        if (websocketClients.containsKey(clientId)) {
            WebSocketConnection conn = websocketClients.get(clientId);
            conn.getHeartbeat().shutdown();
            websocketClients.remove(clientId);
            logger.info("[websocket clientId: {} SessionId: {}]ConnError throwable: {}", clientId, conn.getSession().getId(), throwable.getMessage());
            conn.getSession().close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
        }else {
            logger.warn("clientId {} is not an active websocket connection", clientId);
        }
    }


    // Heartbeat to websocket every 10s
    public static class Heartbeat extends Thread {
        private final WebSocketConnection webSocketConnection;
        private static final Logger logger = LoggerFactory.getLogger(Heartbeat.class);
        private volatile boolean running = true;
        Heartbeat(WebSocketConnection webSocketConnection) {
            this.webSocketConnection = webSocketConnection;
            webSocketConnection.setHeartbeat(this);
        }
        @Override
        public void run() {
            try {
                while (running) {
                    if(System.currentTimeMillis() - webSocketConnection.getLastHeartbeatTime() > WebsocketChannel.heartbeatTimeoutMs) {
                        logger.info("Websocket heartbeat timeout, close conn id: {}", webSocketConnection.getSession().getId());
                        webSocketConnection.getSession().close(new CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE, "Websocket heartbeat timeout."));
                        this.shutdown();
                    }
                    Thread.sleep(10 * 1000);
                    if(webSocketConnection.getSession().isOpen()){
                        webSocketConnection.getSession().getAsyncRemote().sendText(heartbeatStr);
                    }
                }
            } catch (InterruptedException | IOException e) {
                logger.error(e.getMessage());
            }
        }

        public void shutdown() {
            logger.info("Websocket heartbeat shutdown");
            running = false;
        }
    }
}


