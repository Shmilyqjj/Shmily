package redis.client;

import redis.clients.jedis.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author shmily
 * Notice: Jedis是非线程安全的，多线程下会连接失败导致无法查询缓存 （报错如attempting to read from a broken connection）
 * 该代码版本基于JedisPool连接池来保证线程安全
 */

public class JedisPoolClient {

    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private static final CountDownLatch latch = new CountDownLatch(20);

    private static final JedisClientConfig clientConfig = DefaultJedisClientConfig.builder().password("123456").build();

    private static final JedisPool jedisPool = new JedisPool(new HostAndPort("localhost", 6379), clientConfig);




    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for(int i=0;i<20;i++){
            pool.execute(new RedisTest());
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - start);
    }


    static class RedisTest implements Runnable {

        @Override
        public void run() {
            // 并发写
            Jedis jedis = jedisPool.getResource();
            int i = 1000;
            try{
                while(i-->0){
                    jedis.set("hello", "world");
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            }finally{
                jedis.close();
                latch.countDown();
            }
        }
    }
}




