package flink.study.streaming.source.pojo;

import java.sql.Timestamp;

/**
 * :Description: Event实体类
 * :Author: 佳境Shmily
 * :Create Time: 2022/5/15 20:43
 * :Site: shmily-qjj.top
 */
public class Event {
    // 变量必须是public 并且所有属性都是支持序列化的
    public String user;
    public String url;
    public Long timestamp;

    /**
     * 必须声明无参构造方法 使Flink可以解析POJO类
     */
    public Event() {
    }

    /**
     * 带参数构造方法方便自用
     * @param user 用户名
     * @param url 访问地址
     * @param timestamp 访问时间
     */
    public Event(String user, String url, Long timestamp) {
        this.user = user;
        this.url = url;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "user='" + user + '\'' +
                ", url='" + url + '\'' +
                ", timestamp=" + new Timestamp(timestamp) +
                '}';
    }
}
