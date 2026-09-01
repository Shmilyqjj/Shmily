package com.shmily.druid.lineage.schema;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.repository.SchemaRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 静态 DDL 列表 Schema 提供者（内存中的建表语句）。
 */
public class StaticDdlSchemaProvider implements SchemaProvider {

    private final List<String> ddlStatements;

    public StaticDdlSchemaProvider(String... ddlStatements) {
        this(ddlStatements == null ? Collections.<String>emptyList()
                : Arrays.asList(ddlStatements));
    }

    public StaticDdlSchemaProvider(List<String> ddlStatements) {
        this.ddlStatements = ddlStatements == null
                ? Collections.<String>emptyList()
                : Collections.unmodifiableList(new ArrayList<String>(ddlStatements));
    }

    public List<String> getDdlStatements() {
        return ddlStatements;
    }

    @Override
    public void enrich(SchemaRepository repository, DbType dbType) {
        SchemaUtils.applyDdlStatements(repository, ddlStatements);
    }
}
