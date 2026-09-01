package com.shmily.druid;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.shmily.druid.lineage.ColumnLineage;
import com.shmily.druid.lineage.SqlLineageAnalyzer;
import com.shmily.druid.lineage.SourceColumn;
import com.shmily.druid.lineage.SupportedDbType;
import com.shmily.druid.lineage.schema.JdbcSchemaProvider;
import com.shmily.druid.lineage.schema.SchemaProviders;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SqlLineageAnalyzerTest {

    @Test
    public void testSelectWithJoin() {
        String sql = "SELECT u.id AS user_id, u.name AS user_name, o.amount "
                + "FROM ods_user u INNER JOIN ods_order o ON u.id = o.user_id";

        SqlLineageAnalyzer analyzer = new SqlLineageAnalyzer(SupportedDbType.MYSQL, Arrays.asList(
                "CREATE TABLE ods_user (id BIGINT, name VARCHAR(64))",
                "CREATE TABLE ods_order (user_id BIGINT, amount DECIMAL(18,2))"
        ));

        List<ColumnLineage> lineages = analyzer.analyze(sql);
        Assert.assertEquals(3, lineages.size());
        Assert.assertTrue(containsSource(lineages.get(0), "ods_user", "id"));
        Assert.assertTrue(containsSource(lineages.get(2), "ods_order", "amount"));
    }

    @Test
    public void testInsertSelect() {
        String sql = "INSERT INTO target_tbl (uid, uname) SELECT id, name FROM src_user";
        SqlLineageAnalyzer analyzer = new SqlLineageAnalyzer(SupportedDbType.MYSQL);
        List<ColumnLineage> lineages = analyzer.analyze(sql);
        Assert.assertEquals(2, lineages.size());
        Assert.assertEquals("target_tbl", lineages.get(0).getTargetTable());
    }

    @Test
    public void testHiveDialect() {
        String sql = "SELECT a.col1, b.col2 FROM db.table_a a JOIN db.table_b b ON a.key = b.key";
        SqlLineageAnalyzer analyzer = new SqlLineageAnalyzer(SupportedDbType.HIVE);
        List<ColumnLineage> lineages = analyzer.analyze(sql);
        Assert.assertEquals(2, lineages.size());
    }

    @Test
    public void testSchemaProviderInterface() {
        JdbcSchemaProvider provider = new JdbcSchemaProvider() {
            @Override
            protected List<String> loadDdlStatements(DbType dbType) {
                return Arrays.asList("CREATE TABLE t1 (id BIGINT, name VARCHAR(64))");
            }
        };

        SchemaRepository repository = new SchemaRepository(DbType.mysql);
        provider.enrich(repository, DbType.mysql);
        Assert.assertNotNull(repository.findTable("t1"));

        SqlLineageAnalyzer analyzer = new SqlLineageAnalyzer(
                SupportedDbType.MYSQL,
                SchemaProviders.composite(SchemaProviders.empty(), provider)
        );
        List<ColumnLineage> lineages = analyzer.analyze("SELECT id, name FROM t1");
        Assert.assertEquals(2, lineages.size());
        Assert.assertTrue(containsSource(lineages.get(0), "t1", "id"));
    }

    private boolean containsSource(ColumnLineage lineage, String table, String column) {
        for (SourceColumn source : lineage.getSourceColumns()) {
            if (column.equals(source.getColumn())) {
                if (table == null || table.equals(source.getTable())) {
                    return true;
                }
            }
        }
        return false;
    }
}
