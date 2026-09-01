package com.shmily.druid.lineage.schema;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.repository.SchemaRepository;

/**
 * 基于 JDBC 元数据补全 Schema 的抽象基类（预留扩展点）。
 * <p>
 * 子类需实现 {@link #loadDdlStatements(DbType)}，从数据库元数据或 information_schema
 * 生成 CREATE TABLE 语句，再交由父类写入 {@link SchemaRepository}。
 * <p>
 * 示例扩展方向：
 * <ul>
 *   <li>从 MySQL information_schema 反查建表语句</li>
 *   <li>从 Hive Metastore 拉取表结构</li>
 *   <li>从数据目录 / 元数据服务 API 获取 DDL</li>
 * </ul>
 */
public abstract class JdbcSchemaProvider implements SchemaProvider {

    @Override
    public void enrich(SchemaRepository repository, DbType dbType) {
        SchemaUtils.applyDdlStatements(repository, loadDdlStatements(dbType));
    }

    /**
     * 加载与当前方言相关的 DDL 语句列表。
     * 子类在此接入 JDBC / Metastore / 外部 API 等数据源。
     */
    protected abstract java.util.List<String> loadDdlStatements(DbType dbType);
}
