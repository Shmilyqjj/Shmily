package com.shmily.druid.lineage;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.shmily.druid.lineage.schema.SchemaProvider;
import com.shmily.druid.lineage.schema.StaticDdlSchemaProvider;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于 Alibaba Druid SQL Parser 的字段级血缘解析器。
 */
public class SqlLineageAnalyzer {

    private final DbType dbType;
    private final SchemaRepository schemaRepository;

    public SqlLineageAnalyzer(DbType dbType) {
        this(dbType, (SchemaProvider) null);
    }

    public SqlLineageAnalyzer(SupportedDbType dbType) {
        this(dbType.toDbType(), (SchemaProvider) null);
    }

    public SqlLineageAnalyzer(DbType dbType, List<String> ddlStatements) {
        this(dbType, ddlStatements == null || ddlStatements.isEmpty()
                ? null
                : new StaticDdlSchemaProvider(ddlStatements));
    }

    public SqlLineageAnalyzer(SupportedDbType dbType, List<String> ddlStatements) {
        this(dbType.toDbType(), ddlStatements == null || ddlStatements.isEmpty()
                ? null
                : new StaticDdlSchemaProvider(ddlStatements));
    }

    public SqlLineageAnalyzer(DbType dbType, SchemaProvider schemaProvider) {
        this(dbType, null, schemaProvider);
    }

    public SqlLineageAnalyzer(SupportedDbType dbType, SchemaProvider schemaProvider) {
        this(dbType.toDbType(), null, schemaProvider);
    }

    public SqlLineageAnalyzer(DbType dbType, SchemaRepository schemaRepository, SchemaProvider schemaProvider) {
        if (dbType == null) {
            throw new IllegalArgumentException("dbType 不能为空");
        }
        this.dbType = dbType;
        this.schemaRepository = schemaRepository != null ? schemaRepository : new SchemaRepository(dbType);
        if (schemaProvider != null) {
            schemaProvider.enrich(this.schemaRepository, this.dbType);
        }
    }

    public List<ColumnLineage> analyze(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, dbType);
        if (statements.isEmpty()) {
            return Collections.emptyList();
        }
        if (statements.size() > 1) {
            throw new IllegalArgumentException("暂不支持多语句 SQL，请拆分后逐条解析");
        }

        SQLStatement statement = statements.get(0);
        schemaRepository.resolve(statement);

        if (statement instanceof SQLSelectStatement) {
            return analyzeSelect(null, ((SQLSelectStatement) statement).getSelect());
        }
        if (statement instanceof SQLInsertStatement) {
            return analyzeInsert((SQLInsertStatement) statement);
        }
        if (statement instanceof SQLCreateTableStatement) {
            return analyzeCreateTable((SQLCreateTableStatement) statement);
        }
        if (statement instanceof SQLUpdateStatement) {
            return analyzeUpdate((SQLUpdateStatement) statement);
        }
        throw new IllegalArgumentException("暂不支持的 SQL 类型: " + statement.getClass().getSimpleName());
    }

    public DbType getDbType() {
        return dbType;
    }

    private List<ColumnLineage> analyzeInsert(SQLInsertStatement insert) {
        String targetTable = extractTableName(insert.getTableSource());
        SQLSelect select = insert.getQuery();
        if (select == null) {
            return Collections.emptyList();
        }

        List<SQLExpr> targetColumns = insert.getColumns();
        List<SQLSelectItem> selectItems = extractSelectItems(select);
        List<ColumnLineage> result = new ArrayList<ColumnLineage>();

        if (targetColumns == null || targetColumns.isEmpty()) {
            for (SQLSelectItem item : selectItems) {
                String targetColumn = resolveSelectItemAlias(item);
                result.add(buildLineage(targetTable, targetColumn, item.getExpr(), select));
            }
            return result;
        }

        int size = Math.min(targetColumns.size(), selectItems.size());
        for (int i = 0; i < size; i++) {
            String targetColumn = extractColumnName(targetColumns.get(i));
            SQLSelectItem item = selectItems.get(i);
            result.add(buildLineage(targetTable, targetColumn, item.getExpr(), select));
        }
        return result;
    }

    private List<ColumnLineage> analyzeCreateTable(SQLCreateTableStatement create) {
        String targetTable = extractTableName(create.getTableSource());
        SQLSelect select = create.getSelect();
        if (select == null) {
            return Collections.emptyList();
        }
        return analyzeSelect(targetTable, select);
    }

    private List<ColumnLineage> analyzeUpdate(SQLUpdateStatement update) {
        String targetTable = extractTableName(update.getTableSource());
        Map<String, String> aliasMap = buildAliasMap(update.getTableSource());
        List<ColumnLineage> result = new ArrayList<ColumnLineage>();
        for (SQLUpdateSetItem item : update.getItems()) {
            String targetColumn = extractColumnName(item.getColumn());
            List<SourceColumn> sources = extractSourceColumns(item.getValue(), aliasMap, update.getTableSource());
            result.add(new ColumnLineage(targetTable, targetColumn, SQLUtils.toSQLString(item.getValue(), dbType), sources));
        }
        return result;
    }

    private List<ColumnLineage> analyzeSelect(String targetTable, SQLSelect select) {
        List<ColumnLineage> result = new ArrayList<ColumnLineage>();
        for (SQLSelectItem item : extractSelectItems(select)) {
            String targetColumn = resolveSelectItemAlias(item);
            result.add(buildLineage(targetTable, targetColumn, item.getExpr(), select));
        }
        return result;
    }

    private ColumnLineage buildLineage(String targetTable, String targetColumn, SQLExpr expr, SQLSelect select) {
        Map<String, String> aliasMap = buildAliasMap(select.getQueryBlock() != null
                ? select.getQueryBlock().getFrom()
                : null);
        SQLTableSource from = select.getQueryBlock() != null ? select.getQueryBlock().getFrom() : null;
        List<SourceColumn> sources = extractSourceColumns(expr, aliasMap, from);
        return new ColumnLineage(targetTable, targetColumn, SQLUtils.toSQLString(expr, dbType), sources);
    }

    private List<SQLSelectItem> extractSelectItems(SQLSelect select) {
        SQLSelectQuery query = select.getQuery();
        if (query instanceof SQLSelectQueryBlock) {
            return ((SQLSelectQueryBlock) query).getSelectList();
        }
        if (query instanceof SQLUnionQuery) {
            SQLSelectQuery left = ((SQLUnionQuery) query).getLeft();
            if (left instanceof SQLSelectQueryBlock) {
                return ((SQLSelectQueryBlock) left).getSelectList();
            }
        }
        return Collections.emptyList();
    }

    private List<SourceColumn> extractSourceColumns(SQLExpr expr, Map<String, String> aliasMap, SQLTableSource scope) {
        SourceColumnCollector collector = new SourceColumnCollector(aliasMap, scope);
        if (expr != null) {
            expr.accept(collector);
        }
        return collector.getColumns();
    }

    private String resolveSelectItemAlias(SQLSelectItem item) {
        if (item.getAlias() != null && !item.getAlias().isEmpty()) {
            return item.getAlias();
        }
        return item.computeAlias();
    }

    private String extractTableName(SQLTableSource tableSource) {
        if (tableSource == null) {
            return null;
        }
        if (tableSource instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();
            if (expr instanceof SQLName) {
                return ((SQLName) expr).getSimpleName();
            }
            return SQLUtils.toSQLString(expr, dbType);
        }
        return tableSource.getAlias();
    }

    private String extractColumnName(SQLExpr expr) {
        if (expr instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) expr).getName();
        }
        if (expr instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) expr).getName();
        }
        return SQLUtils.toSQLString(expr, dbType);
    }

    private Map<String, String> buildAliasMap(SQLTableSource tableSource) {
        Map<String, String> aliasMap = new LinkedHashMap<String, String>();
        collectAlias(tableSource, aliasMap);
        return aliasMap;
    }

    private void collectAlias(SQLTableSource tableSource, Map<String, String> aliasMap) {
        if (tableSource == null) {
            return;
        }
        if (tableSource instanceof SQLExprTableSource) {
            SQLExprTableSource exprTable = (SQLExprTableSource) tableSource;
            String tableName = extractTableName(exprTable);
            String alias = exprTable.getAlias();
            if (alias != null && !alias.isEmpty()) {
                aliasMap.put(normalize(alias), tableName);
            } else if (tableName != null) {
                aliasMap.put(normalize(tableName), tableName);
            }
            return;
        }
        if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) tableSource;
            collectAlias(join.getLeft(), aliasMap);
            collectAlias(join.getRight(), aliasMap);
            return;
        }
        if (tableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource sub = (SQLSubqueryTableSource) tableSource;
            String alias = sub.getAlias();
            if (alias != null && !alias.isEmpty()) {
                aliasMap.put(normalize(alias), alias);
            }
        }
    }

    private static String normalize(String name) {
        return name == null ? null : name.toLowerCase();
    }

    private String resolveTableName(String owner, Map<String, String> aliasMap) {
        if (owner == null || owner.isEmpty()) {
            return null;
        }
        String key = normalize(owner);
        if (aliasMap.containsKey(key)) {
            return aliasMap.get(key);
        }
        return owner;
    }

    private String resolveFromSchema(SQLPropertyExpr propertyExpr) {
        if (propertyExpr.getResolvedColumn() != null && propertyExpr.getResolvedColumn().getParent() != null) {
            SQLObject parent = propertyExpr.getResolvedColumn().getParent();
            if (parent instanceof SQLCreateTableStatement) {
                SQLCreateTableStatement create = (SQLCreateTableStatement) parent;
                if (create.getName() != null) {
                    return create.getName().getSimpleName();
                }
            }
        }
        return null;
    }

    private class SourceColumnCollector extends SQLASTVisitorAdapter {

        private final Map<String, String> aliasMap;
        private final SQLTableSource scope;
        private final List<SourceColumn> columns = new ArrayList<SourceColumn>();

        SourceColumnCollector(Map<String, String> aliasMap, SQLTableSource scope) {
            this.aliasMap = aliasMap;
            this.scope = scope;
        }

        List<SourceColumn> getColumns() {
            return columns;
        }

        @Override
        public boolean visit(SQLPropertyExpr x) {
            String column = x.getName();
            String owner = x.getOwnernName();
            String table = resolveFromSchema(x);
            if (table == null) {
                table = resolveTableName(owner, aliasMap);
            }
            addColumn(table, column, SQLUtils.toSQLString(x, dbType));
            return false;
        }

        @Override
        public boolean visit(SQLIdentifierExpr x) {
            if (isFunctionOrKeyword(x.getName())) {
                return false;
            }
            String table = null;
            String column = x.getName();
            if (scope != null && scope.findColumn(x.nameHashCode64()) != null) {
                SQLTableSource owner = scope.findTableSourceWithColumn(x.nameHashCode64());
                if (owner != null) {
                    table = extractTableName(owner);
                }
            }
            addColumn(table, column, SQLUtils.toSQLString(x, dbType));
            return false;
        }

        @Override
        public boolean visit(SQLAllColumnExpr x) {
            addColumn("*", "*", "*");
            return false;
        }

        @Override
        public boolean visit(SQLAggregateExpr x) {
            for (SQLExpr arg : x.getArguments()) {
                arg.accept(this);
            }
            return false;
        }

        @Override
        public boolean visit(SQLMethodInvokeExpr x) {
            if (x.getOwner() != null) {
                x.getOwner().accept(this);
            }
            for (SQLExpr arg : x.getArguments()) {
                arg.accept(this);
            }
            return false;
        }

        @Override
        public boolean visit(SQLBinaryOpExpr x) {
            x.getLeft().accept(this);
            x.getRight().accept(this);
            return false;
        }

        @Override
        public boolean visit(SQLCaseExpr x) {
            if (x.getValueExpr() != null) {
                x.getValueExpr().accept(this);
            }
            for (SQLCaseExpr.Item item : x.getItems()) {
                item.getConditionExpr().accept(this);
                item.getValueExpr().accept(this);
            }
            if (x.getElseExpr() != null) {
                x.getElseExpr().accept(this);
            }
            return false;
        }

        @Override
        public boolean visit(SQLCastExpr x) {
            x.getExpr().accept(this);
            return false;
        }

        @Override
        public boolean visit(SQLQueryExpr x) {
            return true;
        }

        @Override
        public boolean visit(SQLCharExpr x) {
            return false;
        }

        @Override
        public boolean visit(SQLIntegerExpr x) {
            return false;
        }

        @Override
        public boolean visit(SQLVariantRefExpr x) {
            return false;
        }

        private void addColumn(String table, String column, String expression) {
            columns.add(new SourceColumn(table, column, expression));
        }

        private boolean isFunctionOrKeyword(String name) {
            if (name == null) {
                return true;
            }
            String upper = name.toUpperCase();
            return "AND".equals(upper) || "OR".equals(upper) || "NOT".equals(upper)
                    || "AS".equals(upper) || "ON".equals(upper) || "IN".equals(upper);
        }
    }
}
