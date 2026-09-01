package com.shmily.druid.lineage;

import java.util.Objects;

/**
 * 血缘中的源字段。
 */
public class SourceColumn {

    private final String table;
    private final String column;
    private final String expression;

    public SourceColumn(String table, String column) {
        this(table, column, null);
    }

    public SourceColumn(String table, String column, String expression) {
        this.table = table;
        this.column = column;
        this.expression = expression;
    }

    public String getTable() {
        return table;
    }

    public String getColumn() {
        return column;
    }

    public String getExpression() {
        return expression;
    }

    public String fullName() {
        if (table == null || table.isEmpty()) {
            return column;
        }
        return table + "." + column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SourceColumn)) {
            return false;
        }
        SourceColumn that = (SourceColumn) o;
        return Objects.equals(table, that.table) && Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, column);
    }

    @Override
    public String toString() {
        return fullName();
    }
}
