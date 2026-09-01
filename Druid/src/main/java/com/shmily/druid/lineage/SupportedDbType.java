package com.shmily.druid.lineage;

import com.alibaba.druid.DbType;

/**
 * Druid SQL Parser 支持的方言类型（常用子集）。
 * <p>
 * 完整列表见 {@link DbType}，此处列出数据血缘解析中推荐使用的方言。
 * 使用时传入 {@link #name()}，例如 {@code "mysql"}、{@code "hive"}。
 */
public enum SupportedDbType {

    MYSQL(DbType.mysql, "MySQL / MariaDB / TiDB / StarRocks / GoldenDB"),
    ORACLE(DbType.oracle, "Oracle / OceanBase Oracle 模式"),
    POSTGRESQL(DbType.postgresql, "PostgreSQL / Greenplum / GaussDB / HighGo"),
    SQLSERVER(DbType.sqlserver, "Microsoft SQL Server / Sybase"),
    DB2(DbType.db2, "IBM DB2"),
    HIVE(DbType.hive, "Apache Hive / Spark SQL (Hive 方言)"),
    SPARK(DbType.antspark, "AntSpark / Spark SQL"),
    PRESTO(DbType.presto, "Presto (旧版)"),
    TRINO(DbType.trino, "Trino"),
    CLICKHOUSE(DbType.clickhouse, "ClickHouse"),
    ODPS(DbType.odps, "MaxCompute (ODPS)"),
    PHOENIX(DbType.phoenix, "Apache Phoenix"),
    H2(DbType.h2, "H2 Database"),
    SQLITE(DbType.sqlite, "SQLite"),
    DM(DbType.dm, "达梦 DM"),
    KINGBASE(DbType.kingbase, "人大金仓 Kingbase"),
    OCEANBASE(DbType.oceanbase, "OceanBase MySQL 模式"),
    POLARDB(DbType.polardb, "PolarDB");

    private final DbType dbType;
    private final String description;

    SupportedDbType(DbType dbType, String description) {
        this.dbType = dbType;
        this.description = description;
    }

    public DbType toDbType() {
        return dbType;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 按名称解析方言，支持枚举名（MYSQL）或小写 dbType 名（mysql）。
     */
    public static SupportedDbType fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("dbType 不能为空");
        }
        String normalized = name.trim();
        for (SupportedDbType type : values()) {
            if (type.name().equalsIgnoreCase(normalized)
                    || type.dbType.name().equalsIgnoreCase(normalized)) {
                return type;
            }
        }
        DbType raw = DbType.of(normalized);
        if (raw != null) {
            for (SupportedDbType type : values()) {
                if (type.dbType == raw) {
                    return type;
                }
            }
            throw new IllegalArgumentException(
                    "方言 " + name + " 在 Druid 中存在，但未列入 SupportedDbType 推荐列表，"
                            + "请直接使用 DbType.of(\"" + normalized + "\")");
        }
        throw new IllegalArgumentException("不支持的方言类型: " + name);
    }

    public static String supportedList() {
        StringBuilder sb = new StringBuilder();
        for (SupportedDbType type : values()) {
            sb.append("  - ").append(type.name())
                    .append(" (").append(type.dbType.name()).append("): ")
                    .append(type.description).append('\n');
        }
        return sb.toString();
    }
}
