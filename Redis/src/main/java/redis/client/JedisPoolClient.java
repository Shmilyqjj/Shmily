package redis.client;

import redis.clients.jedis.*;


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

    public String get(String key) {
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

    public long setTtl(String key,long ttlSeconds) {
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

    public long getTtl(String key) {
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


        redisUtils.close();
    }
}
