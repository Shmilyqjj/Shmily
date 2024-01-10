package top.shmilyqjj.springboot.models.entity;

import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import top.shmilyqjj.springboot.controller.channel.WebsocketChannel;

/**
 * @author Shmily
 * @Description: websocket client实体
 * @CreateTime: 2024/1/10 下午2:51
 */

@Data
@AllArgsConstructor
public class WebSocketConnection {
    private String clientId;
    private Session session;
    private long lastHeartbeatTime;
    private WebsocketChannel.Heartbeat heartbeat;
}
