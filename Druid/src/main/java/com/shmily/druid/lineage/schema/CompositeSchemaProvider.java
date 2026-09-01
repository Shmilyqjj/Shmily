package com.shmily.druid.lineage.schema;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.repository.SchemaRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 组合多个 SchemaProvider，按顺序依次补全。
 */
public class CompositeSchemaProvider implements SchemaProvider {

    private final List<SchemaProvider> providers;

    public CompositeSchemaProvider(SchemaProvider... providers) {
        this(providers == null ? Collections.<SchemaProvider>emptyList() : Arrays.asList(providers));
    }

    public CompositeSchemaProvider(List<SchemaProvider> providers) {
        this.providers = providers == null
                ? Collections.<SchemaProvider>emptyList()
                : Collections.unmodifiableList(new ArrayList<SchemaProvider>(providers));
    }

    @Override
    public void enrich(SchemaRepository repository, DbType dbType) {
        for (SchemaProvider provider : providers) {
            if (provider != null) {
                provider.enrich(repository, dbType);
            }
        }
    }
}
