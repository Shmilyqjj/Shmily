# Druid — SQL 字段级血缘解析

基于 [Alibaba Druid SQL Parser](https://github.com/alibaba/druid) 的字段级血缘解析子模块，输入 SQL 与数据源方言，输出目标字段到源字段的映射关系。

## 项目结构

```
Druid/
├── pom.xml
└── src/main/java/com/shmily/druid/
    ├── SqlLineageDemo.java              # 示例入口
    └── lineage/
        ├── SqlLineageAnalyzer.java      # 核心解析类
        ├── SupportedDbType.java         # 支持的方言枚举
        ├── ColumnLineage.java           # 血缘结果
        ├── SourceColumn.java            # 源字段
        └── schema/                      # Schema 补全扩展点
            ├── SchemaProvider.java      # DDL/Schema 补全接口
            ├── StaticDdlSchemaProvider.java
            ├── JdbcSchemaProvider.java  # JDBC 扩展抽象基类（预留）
            ├── CompositeSchemaProvider.java
            ├── EmptySchemaProvider.java
            ├── SchemaProviders.java     # 工厂方法
            └── SchemaUtils.java
```

## 快速开始

### 1. 修改示例代码运行

编辑 `SqlLineageDemo.java` 中的常量：

```java
private static final String DB_TYPE = "mysql";   // 方言
private static final String SQL = "...";          // 待解析 SQL
private static final SchemaProvider SCHEMA_PROVIDER = SchemaProviders.staticDdl(
        "CREATE TABLE ods_user (id BIGINT, name VARCHAR(64))",
        "CREATE TABLE ods_order (user_id BIGINT, amount DECIMAL(18,2))"
);
```

运行：

```bash
cd Druid
mvn exec:java
```

### 2. 代码调用

```java
SupportedDbType dbType = SupportedDbType.fromName("mysql");

// 方式 A：直接传 DDL 列表（简便）
SqlLineageAnalyzer analyzer = new SqlLineageAnalyzer(dbType, ddlList);

// 方式 B：通过 SchemaProvider 补全 Schema（推荐，可扩展）
SqlLineageAnalyzer analyzer2 = new SqlLineageAnalyzer(dbType, schemaProvider);

List<ColumnLineage> lineages = analyzer.analyze(sql);
for (ColumnLineage lineage : lineages) {
    System.out.println(lineage.getTargetTable() + "." + lineage.getTargetColumn()
            + " <- " + lineage.getSourceColumns());
}
```

## 支持的方言（SupportedDbType）

| 枚举名 | Druid dbType | 说明 |
|--------|-------------|------|
| MYSQL | mysql | MySQL / MariaDB / TiDB / StarRocks / GoldenDB |
| ORACLE | oracle | Oracle / OceanBase Oracle 模式 |
| POSTGRESQL | postgresql | PostgreSQL / Greenplum / GaussDB / HighGo |
| SQLSERVER | sqlserver | Microsoft SQL Server / Sybase |
| DB2 | db2 | IBM DB2 |
| HIVE | hive | Apache Hive / Spark SQL (Hive 方言) |
| SPARK | antspark | AntSpark / Spark SQL |
| PRESTO | presto | Presto (旧版) |
| TRINO | trino | Trino |
| CLICKHOUSE | clickhouse | ClickHouse |
| ODPS | odps | MaxCompute (ODPS) |
| PHOENIX | phoenix | Apache Phoenix |
| H2 | h2 | H2 Database |
| SQLITE | sqlite | SQLite |
| DM | dm | 达梦 DM |
| KINGBASE | kingbase | 人大金仓 Kingbase |
| OCEANBASE | oceanbase | OceanBase MySQL 模式 |
| POLARDB | polardb | PolarDB |

完整方言列表见 Druid 的 `com.alibaba.druid.DbType` 枚举。传入 `SupportedDbType.fromName("mysql")` 或枚举名 `MYSQL` 均可。

## 支持的 SQL 类型

| 类型 | 说明 |
|------|------|
| SELECT | 输出列 → 源列 |
| INSERT INTO ... SELECT | 目标表列 → SELECT 源列 |
| CREATE TABLE AS SELECT | 新表列 → SELECT 源列 |
| UPDATE SET col = expr | 目标列 → 表达式中的源列 |

暂不支持多语句 SQL（`;` 分隔的多条语句需拆分后逐条解析）。

## Schema 补全接口（SchemaProvider）

血缘解析中的**列名消歧**（如裸字段 `id` 归属哪张表）依赖 `SchemaRepository` 中的表结构元数据。可通过 `SchemaProvider` 接口从任意来源补全 DDL：

```java
public interface SchemaProvider {
    void enrich(SchemaRepository repository, DbType dbType);
}
```

### 内置实现

| 类 | 用途 |
|----|------|
| `StaticDdlSchemaProvider` | 内存中的静态 DDL 列表 |
| `EmptySchemaProvider` | 不补全 Schema |
| `CompositeSchemaProvider` | 组合多个 Provider，按顺序依次补全 |
| `JdbcSchemaProvider` | **预留抽象基类**，子类实现 `loadDdlStatements()` 接入 JDBC / Metastore |

### 工厂方法（SchemaProviders）

```java
SchemaProvider p1 = SchemaProviders.staticDdl("CREATE TABLE t1 (id BIGINT)");
SchemaProvider p2 = SchemaProviders.empty();
SchemaProvider p3 = SchemaProviders.composite(p2, p1);
```

### 自定义 JDBC / Metastore 扩展示例

```java
public class HiveMetastoreSchemaProvider extends JdbcSchemaProvider {
    @Override
    protected List<String> loadDdlStatements(DbType dbType) {
        // TODO: 从 Hive Metastore / JDBC / 元数据 API 拉取建表语句
        return fetchDdlFromMetastore();
    }
}

SqlLineageAnalyzer analyzer = new SqlLineageAnalyzer(
        SupportedDbType.HIVE,
        new HiveMetastoreSchemaProvider()
);
```

也可直接实现 `SchemaProvider`，在 `enrich()` 中写入任意 DDL 或调用 `SchemaUtils.applyDdlStatements()`。

## 输出示例

输入：

```sql
INSERT INTO dw_user_summary (user_id, user_name, total_amount)
SELECT u.id, u.name, SUM(o.amount) AS total_amount
FROM ods_user u
INNER JOIN ods_order o ON u.id = o.user_id
GROUP BY u.id, u.name
```

输出：

```
dw_user_summary.user_id <- ods_user.id  [expr: u.id]
dw_user_summary.user_name <- ods_user.name  [expr: u.name]
dw_user_summary.total_amount <- ods_order.amount  [expr: SUM(o.amount)]
```

## 编译与测试

```bash
# 仅编译 Druid 模块
mvn --settings /usr/lib/java/maven/apache-maven-3.8.8/conf/settings.xml \
    -Dmaven.repo.local=/home/shmily/Tools/maven/maven_repository \
    -pl Druid -am test compile

# 运行单元测试
mvn -pl Druid test

# 运行示例
mvn -pl Druid exec:java
```

**涉及 module：** `Shmily/Druid`（及父 POM `Shmily/pom.xml`）

**依赖：** `com.alibaba:druid:1.2.23`

本模块为本地工具/库，无需部署服务。

## 已知限制

- 复杂嵌套子查询、深层 CTE 场景下，Druid 可能无法完整回溯列所属表
- `SELECT *` 无法自动展开为具体字段
- 提供 DDL Schema 可显著提升列名消歧准确度，建议在 production 接入 `SchemaProvider` 从元数据服务加载
