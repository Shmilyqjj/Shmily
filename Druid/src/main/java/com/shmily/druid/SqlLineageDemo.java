package com.shmily.druid;

import com.shmily.druid.lineage.ColumnLineage;
import com.shmily.druid.lineage.SqlLineageAnalyzer;
import com.shmily.druid.lineage.SupportedDbType;
import com.shmily.druid.lineage.schema.SchemaProvider;
import com.shmily.druid.lineage.schema.SchemaProviders;

import java.util.Arrays;
import java.util.List;

/**
 * 字段级 SQL 血缘解析示例。修改 DB_TYPE 和 SQL 后直接运行 main。
 */
public class SqlLineageDemo {

    private static final String DB_TYPE = "mysql";

    private static final String SQL =
            "INSERT INTO dw_user_summary (user_id, user_name, total_amount)\n"
                    + "SELECT u.id,\n"
                    + "       u.name,\n"
                    + "       SUM(o.amount) AS total_amount\n"
                    + "FROM ods_user u\n"
                    + "INNER JOIN ods_order o ON u.id = o.user_id\n"
                    + "GROUP BY u.id, u.name";

    /**
     * Schema 补全：可替换为自定义 SchemaProvider（JDBC / Metastore / 文件等）。
     */
    private static final SchemaProvider SCHEMA_PROVIDER = SchemaProviders.staticDdl(
            "CREATE TABLE ods_user (id BIGINT, name VARCHAR(64), age INT)",
            "CREATE TABLE ods_order (user_id BIGINT, amount DECIMAL(18,2), order_date DATE)",
            "CREATE TABLE dw_user_summary (user_id BIGINT, user_name VARCHAR(64), total_amount DECIMAL(18,2))"
    );

    public static void main(String[] args) {
        System.out.println("=== 支持的方言类型 ===");
        System.out.println(SupportedDbType.supportedList());

        SupportedDbType dbType = SupportedDbType.fromName(DB_TYPE);
        SqlLineageAnalyzer analyzer = new SqlLineageAnalyzer(dbType, SCHEMA_PROVIDER);

        System.out.println("=== 解析参数 ===");
        System.out.println("方言: " + dbType.name() + " (" + dbType.getDescription() + ")");
        System.out.println("SQL:\n" + SQL);
        System.out.println();

        List<ColumnLineage> lineages = analyzer.analyze(SQL);

        System.out.println("=== 字段级血缘结果 (" + lineages.size() + " 条) ===");
        for (int i = 0; i < lineages.size(); i++) {
            ColumnLineage lineage = lineages.get(i);
            System.out.println((i + 1) + ". " + lineage);
            if (!lineage.getSourceColumns().isEmpty()) {
                System.out.println("   源字段明细:");
                for (int j = 0; j < lineage.getSourceColumns().size(); j++) {
                    System.out.println("     - " + lineage.getSourceColumns().get(j).fullName());
                }
            }
        }
    }
}
