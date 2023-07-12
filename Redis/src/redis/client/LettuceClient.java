package redis.client;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author shmily
 */
public class LettuceClient {
    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> redisCommands;

    public LettuceClient(String host, int port, CharSequence password, int timeoutSeconds){
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

    public Long hDelete(String key, String... fields) {
        return redisCommands.hdel(key,fields);
    }

    public void close() {
        connection.close();
        redisClient.shutdown();
    }


    public static void main(String[] args) {
        LettuceClient lettuceClient = new LettuceClient("localhost", 6379, "123456", 10);

        lettuceClient.set("key", "value");
        String value = lettuceClient.get("key");
        System.out.println("Value: " + value);

        boolean exists = lettuceClient.exists("key");
        System.out.println("Exists: " + exists);

        lettuceClient.delete("key");
        exists = lettuceClient.exists("key");
        System.out.println("Exists: " + exists);

        lettuceClient.hSet("key", "col", "111");
        System.out.println(lettuceClient.hGet("key", "col"));

        lettuceClient.close();
    }

}
