package redis.client;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

/**
 * @author shmily
 */
public class LettuceClient {
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> redisCommands;

    private final String host;
    private final int port;
    private final CharSequence password;
    private final int timeoutSeconds;

    public LettuceClient(String host, int port, CharSequence password, int timeoutSeconds){
        this.host = host;
        this.port = port;
        this.password = password;
        this.timeoutSeconds = timeoutSeconds;
        RedisURI redisUri = RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withPassword(password)
                .withTimeout(Duration.of(timeoutSeconds, ChronoUnit.SECONDS))
                .build();
        this.redisClient = RedisClient.create(redisUri);
        this.connection = redisClient.connect();
        this.redisCommands = connection.sync();
    }


    public void set(String key, String value) {
        redisCommands.set(key, value);
    }

    public String get(String key) {
        return redisCommands.get(key);
    }

    public boolean setWithTtl(String key, String value, long ttlSeconds) {
        // 同时设置key和ttl 原子操作
        return "OK".equals(redisCommands.setex(key, ttlSeconds, value));
    }

    public boolean setTtl(String key,long ttlSeconds) {
        return redisCommands.expire(key, ttlSeconds);
    }

    public boolean rmTtl(String key) {
        return redisCommands.persist(key);
    }

    public long getTtl(String key) {
        return redisCommands.ttl(key);
    }

    public boolean exists(String key) {
        return redisCommands.exists(key) != 0;
    }

    public void delete(String key) {
        redisCommands.del(key);
    }

    public void hSet(String key, String field, String value) {
        redisCommands.hset(key, field, value);
    }

    public String hGet(String key, String field) {
        return redisCommands.hget(key,field);
    }

    public boolean hExists(String key, String field) {
        return redisCommands.hexists(key, field);
    }

    public Long hDelete(String key, String... fields) {
        return redisCommands.hdel(key,fields);
    }

    public void close() {
        connection.close();
        redisClient.shutdown();
    }

    public void reconnect() {
        // io.lettuce.core.RedisCommandTimeoutException: Command timed out after 1 second(s)
        // 捕获异常RedisCommandTimeoutException并调用重连
        if (connection.isOpen()) {
            close();
        }
        RedisURI redisUri = RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withPassword(password)
                .withTimeout(Duration.of(timeoutSeconds, ChronoUnit.SECONDS))
                .build();
        redisClient = RedisClient.create(redisUri);
        connection = redisClient.connect();
        redisCommands = connection.sync();
    }


    public static void main(String[] args) {
        LettuceClient lettuceClient = new LettuceClient("localhost", 6379, "123456", 6);

        System.out.println("=======set=======");
        lettuceClient.set("key", "value");
        String value = lettuceClient.get("key");
        System.out.println("Value: " + value);

        System.out.println("=======exists=======");
        boolean exists = lettuceClient.exists("key");
        System.out.println("Exists: " + exists);

        lettuceClient.delete("key");
        exists = lettuceClient.exists("key");
        System.out.println("Exists: " + exists);

        System.out.println("=======setex=======");
        System.out.println(lettuceClient.setWithTtl("kt","vt", 10));
        System.out.println(lettuceClient.get("kt"));

        System.out.println("=======hset=======");
        lettuceClient.hSet("key", "col", "111");
        System.out.println("hExists: " + lettuceClient.hExists("key", "col"));
        System.out.println(lettuceClient.hGet("key", "col"));

        System.out.println("=======ttl=======");
        System.out.println("TTL: " + lettuceClient.getTtl("key"));
        System.out.println(lettuceClient.setTtl("key", 20));
        System.out.println("TTL After set: " + lettuceClient.getTtl("key"));
        System.out.println(lettuceClient.rmTtl("key"));
        System.out.println("TTL After rm: " + lettuceClient.getTtl("key"));

        System.out.println("=======reconnect=======");
        lettuceClient.set("a","b");
        lettuceClient.close();
        lettuceClient.reconnect();
        lettuceClient.set("aaa", "bbb");
        System.out.println("After reconnect:" + lettuceClient.get("aaa"));

    }

}
