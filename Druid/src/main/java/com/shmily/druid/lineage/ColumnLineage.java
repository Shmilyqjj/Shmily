package com.shmily.druid.lineage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 单条字段级血缘：一个目标字段及其来源字段集合。
 */
public class ColumnLineage {

    private final String targetTable;
    private final String targetColumn;
    private final String expression;
    private final List<SourceColumn> sourceColumns;

    public ColumnLineage(String targetTable, String targetColumn, String expression,
                         List<SourceColumn> sourceColumns) {
        this.targetTable = targetTable;
        this.targetColumn = targetColumn;
        this.expression = expression;
        this.sourceColumns = deduplicate(sourceColumns);
    }

    private static List<SourceColumn> deduplicate(List<SourceColumn> columns) {
        if (columns == null || columns.isEmpty()) {
            return Collections.emptyList();
        }
        Set<SourceColumn> set = new LinkedHashSet<>(columns);
        return new ArrayList<>(set);
    }

    public String getTargetTable() {
        return targetTable;
    }

    public String getTargetColumn() {
        return targetColumn;
    }

    public String getExpression() {
        return expression;
    }

    public List<SourceColumn> getSourceColumns() {
        return sourceColumns;
    }

    public String targetFullName() {
        if (targetTable == null || targetTable.isEmpty()) {
            return targetColumn;
        }
        return targetTable + "." + targetColumn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(targetFullName()).append(" <- ");
        if (sourceColumns.isEmpty()) {
            sb.append("(无解析到的源字段)");
        } else {
            for (int i = 0; i < sourceColumns.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(sourceColumns.get(i).fullName());
            }
        }
        if (expression != null && !expression.isEmpty()) {
            sb.append("  [expr: ").append(expression).append(']');
        }
        return sb.toString();
    }
}
