package com.shmily.druid.lineage.schema;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.repository.SchemaRepository;

/**
 * DDL / Schema 信息补全接口。
 * <p>
 * 血缘解析中的列名消歧依赖 {@link SchemaRepository} 中的表结构元数据。
 * 实现方可从 JDBC 元数据、Hive Metastore、文件、配置中心、缓存等任意来源加载 DDL，
 * 并通过 {@link #enrich(SchemaRepository, DbType)} 写入 Repository。
 */
public interface SchemaProvider {

    /**
     * 将 Schema 信息补全到 {@link SchemaRepository}。
     *
     * @param repository 与当前解析方言绑定的 SchemaRepository（非 null）
     * @param dbType     当前 SQL 方言（非 null）
     */
    void enrich(SchemaRepository repository, DbType dbType);
}
