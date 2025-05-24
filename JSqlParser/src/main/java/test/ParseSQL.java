package test;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ParseSQL {
    public static void main(String[] args) {
        String sql = "SELECT DISTINCT u.id, r.role_name, u.user_name, u.sex, u.email " +
                "FROM t_user u " +
                "LEFT JOIN t_role r ON u.role_id = r.id " +
                "WHERE r.role_name = '管理员' " +
                "ORDER BY u.age DESC " +
                "LIMIT 0,10";
        parse(sql);
    }

    /**
     * 解析并修改SQL
     * @param sql input sql
     */
    public static void parse(String sql) {
        try {
            Select select = (Select) CCJSqlParserUtil.parse(sql);
            PlainSelect plainSelect = select.getPlainSelect();
            System.out.println("-------------------------原始SQL-------------------------");
            System.out.println("【DISTINCT 子句】：" + plainSelect.getDistinct());
            System.out.println("【查询字段】：" + plainSelect.getSelectItems());
            System.out.println("【FROM 表】：" + plainSelect.getFromItem());
            System.out.println("【WHERE 子句】：" + plainSelect.getWhere());
            System.out.println("【JOIN 子句】：" + plainSelect.getJoins());
            System.out.println("【LIMIT 子句】：" + plainSelect.getLimit());
            System.out.println("【OFFSET 子句】：" + plainSelect.getOffset());
            System.out.println("【ORDER BY 子句】：" + plainSelect.getOrderByElements());
            System.out.println("--------------------------------------------------------");
            // 取消去重
            plainSelect.setDistinct(null);
            // 修改查询字段为 *
            List<SelectItem<?>> selectItems = new ArrayList<>();
            selectItems.add(new SelectItem<>(new AllColumns()));
            plainSelect.setSelectItems(selectItems);
            // 修改 WHERE 子句
            EqualsTo equalsTo = new EqualsTo();
            equalsTo.setLeftExpression(new Column("u.id"));
            equalsTo.setRightExpression(new LongValue(1));
            plainSelect.setWhere(new AndExpression(plainSelect.getWhere(), equalsTo));
            // 修改 LIMIT 子句
            Limit limit = new Limit();
            limit.setRowCount(new LongValue(5));
            limit.setOffset(new LongValue(0));
            plainSelect.setLimit(limit);
            // 修改排序为 u.age ASC
            OrderByElement orderByElement = new OrderByElement();
            orderByElement.setExpression(new Column("u.age"));
            orderByElement.setAsc(true); // 升序
            plainSelect.setOrderByElements(Collections.singletonList(orderByElement));
            System.out.println("处理后SQL: " + plainSelect);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
    }

}

