package com.shmily.druid.lineage.schema;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.repository.SchemaRepository;

/**
 * 空 Schema 提供者，不做任何补全。
 */
public enum EmptySchemaProvider implements SchemaProvider {
    INSTANCE;

    @Override
    public void enrich(SchemaRepository repository, DbType dbType) {
        // no-op
    }
}
