package com.shmily.druid.lineage.schema;

import java.util.List;

/**
 * {@link SchemaProvider} 工厂方法。
 */
public final class SchemaProviders {

    private SchemaProviders() {
    }

    public static SchemaProvider empty() {
        return EmptySchemaProvider.INSTANCE;
    }

    public static SchemaProvider staticDdl(String... ddlStatements) {
        return new StaticDdlSchemaProvider(ddlStatements);
    }

    public static SchemaProvider staticDdl(List<String> ddlStatements) {
        return new StaticDdlSchemaProvider(ddlStatements);
    }

    public static SchemaProvider composite(SchemaProvider... providers) {
        return new CompositeSchemaProvider(providers);
    }

    public static SchemaProvider composite(List<SchemaProvider> providers) {
        return new CompositeSchemaProvider(providers);
    }
}
