package redis.client;

import redis.clients.jedis.*;

import java.io.*;
import java.util.ArrayList;


/**
 * @author shmily
 */
public class JedisPoolClient {
    private final JedisPool jedisPool;

    public JedisPoolClient(String host, int port, String password, int timeoutSec) {
        if (password != null && password.length() != 0) {
            JedisClientConfig clientConfig = DefaultJedisClientConfig.builder().password(password).timeoutMillis(timeoutSec*1000).build();
            this.jedisPool = new JedisPool(new HostAndPort(host, port), clientConfig);
        }else {
            this.jedisPool = new JedisPool(host, port);
        }
    }

    public void set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
        }finally {
            jedis.close();
        }
    }

    public void set(byte[] key, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
        }finally {
            jedis.close();
        }
    }

    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.get(key);
        }finally {
            jedis.close();
        }
    }

    public byte[] get(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.get(key);
        }finally {
            jedis.close();
        }
    }

    public boolean setWithTtl(String key, String value, long ttlSeconds) {
        // 同时设置key和ttl 原子操作
        Jedis jedis = jedisPool.getResource();
        try {
            return "OK".equals(jedis.setex(key, ttlSeconds, value));
        }finally {
            jedis.close();
        }
    }

    public boolean setWithTtl(byte[] key, byte[] value, long ttlSeconds) {
        // 同时设置key和ttl 原子操作
        Jedis jedis = jedisPool.getResource();
        try {
            return "OK".equals(jedis.setex(key, ttlSeconds, value));
        }finally {
            jedis.close();
        }
    }

    public long setTtl(String key,long ttlSeconds) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.expire(key, ttlSeconds);
        }finally {
            jedis.close();
        }
    }

    public long setTtl(byte[] key,long ttlSeconds) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.expire(key, ttlSeconds);
        }finally {
            jedis.close();
        }
    }

    public long rmTtl(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.persist(key);
        }finally {
            jedis.close();
        }
    }

    public long rmTtl(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.persist(key);
        }finally {
            jedis.close();
        }
    }

    public long getTtl(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.ttl(key);
        }finally {
            jedis.close();
        }
    }

    public long getTtl(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.ttl(key);
        }finally {
            jedis.close();
        }
    }

    public void hSet(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hset(key, field, value);
        }finally {
            jedis.close();
        }
    }

    public String hGet(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hget(key,field);
        }finally {
            jedis.close();
        }
    }

    public boolean hExists(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hexists(key, field);
        }finally {
            jedis.close();
        }
    }

    public Long hDelete(String key, String... fields) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hdel(key,fields);
        }finally {
            jedis.close();
        }
    }

    public boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.exists(key);
        }finally {
            jedis.close();
        }
    }

    public void delete(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(key);
        }finally {
            jedis.close();
        }
    }


    // 将 Java 对象序列化为字节数组
    private byte[] serializeObject(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将字节数组反序列化为 Java 对象
    private <T> T deserializeObject(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        jedisPool.close();
    }

    // test
    public static void main(String[] args) {
        JedisPoolClient redisUtils = new JedisPoolClient("localhost", 6379, "123456", 5);

        redisUtils.set("key", "value");
        String value = redisUtils.get("key");
        System.out.println("Value: " + value);

        boolean exists = redisUtils.exists("key");
        System.out.println("Exists: " + exists);

        redisUtils.delete("key");
        exists = redisUtils.exists("key");
        System.out.println("Exists: " + exists);

        System.out.println("setWithTtl return: " + redisUtils.setWithTtl("key", "value", 5));
        exists = redisUtils.exists("key");
        System.out.println("setWithTtl Exists: " + exists + " ttl:" + redisUtils.getTtl("key"));

        redisUtils.hSet("hkey", "col", "111");
        System.out.println(redisUtils.hGet("hkey", "col"));

        // 缓存java object
        ArrayList<String> arrObj = new ArrayList<>();
        arrObj.add("a");
        arrObj.add("b");
        arrObj.add("c");
        byte[] byteValue = redisUtils.serializeObject(arrObj);
        redisUtils.setWithTtl("arr".getBytes(), byteValue, 1000);
        Object o = redisUtils.deserializeObject(redisUtils.get("arr".getBytes()));
        ArrayList<String> arr = (ArrayList<String>) o;
        arr.forEach(System.out::println);

        redisUtils.close();
    }
}
