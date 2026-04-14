package top.shmilyqjj.springboot.config;

import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Connection;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;

/**
 * Redis（RediSearch + 向量）持久化知识库向量；需 Redis Stack 或带搜索/向量模块的 Redis。
 * <p>
 * 排除默认 {@link org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreAutoConfiguration}，
 * 在此注册可过滤元数据字段 {@code kbDocId} / {@code originalFilename}。
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedisVectorStoreProperties.class)
public class KnowledgeRedisVectorStoreConfig {

    @Bean
    @Primary
    public RedisVectorStore vectorStore(
            EmbeddingModel embeddingModel,
            JedisConnectionFactory jedisConnectionFactory,
            RedisVectorStoreProperties props,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<VectorStoreObservationConvention> observationConvention,
            ObjectProvider<BatchingStrategy> batchingStrategy) {

        JedisPooled jedis = createJedisPooled(jedisConnectionFactory);
        RedisVectorStore.Builder builder = RedisVectorStore.builder(jedis, embeddingModel)
                .initializeSchema(props.isInitializeSchema())
                .indexName(props.getIndexName())
                .prefix(props.getPrefix())
                .metadataFields(
                        RedisVectorStore.MetadataField.tag("kbDocId"),
                        RedisVectorStore.MetadataField.text("originalFilename"))
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                .batchingStrategy(batchingStrategy.getIfAvailable(TokenCountBatchingStrategy::new));
        observationConvention.ifAvailable(builder::customObservationConvention);
        return builder.build();
    }

    /**
     * 对齐 Spring AI {@code RedisVectorStoreAutoConfiguration#jedisPooled}，并补充 ACL 用户名。
     * <p>
     * RediSearch 建索引要求逻辑库必须为 {@code 0}（否则会报 {@code Cannot create index on db != 0}），
     * 因此向量连接固定使用 database 0，与 {@code spring.data.redis.database} 可不同。
     */
    private JedisPooled createJedisPooled(JedisConnectionFactory factory) {
        String host = factory.getHostName();
        int port = factory.getPort();
        int configuredDb = factory.getDatabase();
        if (configuredDb != 0) {
            log.warn(
                    "spring.data.redis.database={}：RediSearch 向量索引仅支持 db 0，向量客户端将固定使用 database 0（其它 RedisTemplate 仍用配置库号）",
                    configuredDb);
        }
        DefaultJedisClientConfig.Builder cfg = DefaultJedisClientConfig.builder()
                .ssl(factory.isUseSsl())
                .timeoutMillis(factory.getTimeout())
                .database(0);
        if (StringUtils.hasText(factory.getClientName())) {
            cfg.clientName(factory.getClientName());
        }
        /*
         * 无密码的 Redis 上若仍发送 AUTH（含仅带 default 用户、空密码），会报错：
         * ERR AUTH <password> called without any password configured for the default user
         */
        String password = normalizeSecret(factory.getPassword());
        RedisStandaloneConfiguration standalone = factory.getStandaloneConfiguration();
        String username = standalone != null ? normalizeSecret(standalone.getUsername()) : null;
        if (StringUtils.hasText(username) && "default".equalsIgnoreCase(username) && !StringUtils.hasText(password)) {
            username = null;
        }
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            cfg.user(username).password(password);
        } else if (StringUtils.hasText(password)) {
            cfg.password(password);
        } else if (StringUtils.hasText(username)) {
            log.warn("Redis 已配置 username={} 但未配置 password，向量客户端将不发送认证（请补全密码或去掉用户名）", username);
        }
        JedisClientConfig clientConfig = cfg.build();
        /*
         * 池内连接在 Redis 端超时关闭或网络抖动后仍可能被复用，导致 ftSearch 时出现「断开的管道」。
         * testOnBorrow / testWhileIdle 在取出或空闲巡检时校验连接，失效连接会被丢弃并新建。
         */
        GenericObjectPoolConfig<Connection> pool = new GenericObjectPoolConfig<>();
        pool.setTestOnBorrow(true);
        pool.setTestWhileIdle(true);
        pool.setTimeBetweenEvictionRunsMillis(30_000);
        pool.setMinEvictableIdleTimeMillis(60_000);
        pool.setMaxTotal(64);
        pool.setMaxIdle(32);
        return new JedisPooled(pool, new HostAndPort(host, port), clientConfig);
    }

    private static String normalizeSecret(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}
