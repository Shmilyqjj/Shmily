package redis.client;


import redis.clients.jedis.Jedis;

/**
 * @author shmily
 */
public class JedisClient {
    private final Jedis jedis;

    public JedisClient(String host, int port, String password) {
        this.jedis = new Jedis(host, port);
        if (password != null && password.length() != 0) {
            this.jedis.auth(password);
        }
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    public String get(String key) {
        return jedis.get(key);
    }


    public long setTtl(String key,long ttlSeconds) {
        return jedis.expire(key, ttlSeconds);
    }

    public long rmTtl(String key) {
        return jedis.persist(key);
    }

    public long getTtl(String key) {
        return jedis.ttl(key);
    }

    public void hSet(String key, String field, String value) {
        jedis.hset(key, field, value);
    }

    public String hGet(String key, String field) {
        return jedis.hget(key,field);
    }

    public boolean hExists(String key, String field) {
        return jedis.hexists(key,field);
    }

    public Long hDelete(String key, String... fields) {
        return jedis.hdel(key,fields);
    }

    public boolean exists(String key) {
        return jedis.exists(key);
    }

    public void delete(String key) {
        jedis.del(key);
    }

    public void close() {
        jedis.close();
    }

    // test
    public static void main(String[] args) {
        JedisClient redisUtils = new JedisClient("localhost", 6379, "123456");

        redisUtils.set("key", "value");
        String value = redisUtils.get("key");
        System.out.println("Value: " + value);

        boolean exists = redisUtils.exists("key");
        System.out.println("Exists: " + exists);

        redisUtils.delete("key");
        exists = redisUtils.exists("key");
        System.out.println("Exists: " + exists);

        redisUtils.hSet("key", "col", "111");
        System.out.println("hExists: " + redisUtils.hExists("key", "col"));
        System.out.println(redisUtils.hGet("key", "col"));
        System.out.println("TTL: " + redisUtils.getTtl("key"));
        System.out.println(redisUtils.setTtl("key", 20));
        System.out.println("TTL After set: " + redisUtils.getTtl("key"));
        System.out.println(redisUtils.rmTtl("key"));
        System.out.println("TTL After rm: " + redisUtils.getTtl("key"));


        redisUtils.close();
    }
}
