package com.shmily.druid.lineage.schema;

import com.alibaba.druid.sql.repository.SchemaRepository;

import java.util.List;

/**
 * Schema 加载工具方法。
 */
public final class SchemaUtils {

    private SchemaUtils() {
    }

    /**
     * 将 DDL 语句列表写入 SchemaRepository。
     * 支持 CREATE TABLE、ALTER TABLE、USE database 等 Druid 可解析的 DDL。
     */
    public static void applyDdlStatements(SchemaRepository repository, List<String> ddlStatements) {
        if (repository == null || ddlStatements == null) {
            return;
        }
        for (String ddl : ddlStatements) {
            if (ddl != null && !ddl.trim().isEmpty()) {
                repository.console(ddl.trim());
            }
        }
    }
}
