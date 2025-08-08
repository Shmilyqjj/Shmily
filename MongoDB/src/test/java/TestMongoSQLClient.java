import com.github.vincentrussell.query.mongodb.sql.converter.MongoDBQueryHolder;
import com.github.vincentrussell.query.mongodb.sql.converter.ParseException;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryConverter;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryResultIterator;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import mongo.sql.MongoSQLClient;
import mongo.sql.model.MongoResult;
import org.bson.Document;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * CreateTime: 2025/8/8 12:15
 * Author Shmily
 */
public class TestMongoSQLClient {

    @Test
    public void testQueryConverter() throws ParseException {
        QueryConverter queryConverter = new QueryConverter.Builder().sqlString("select updateTime,createTime,manager,memo,pname,puid,uids from partner").build();
        MongoDBQueryHolder mongoDBQueryHolder = queryConverter.getMongoQuery();
        String collection = mongoDBQueryHolder.getCollection();
        Document query = mongoDBQueryHolder.getQuery();
        Document columns = mongoDBQueryHolder.getProjection();
        Document projection = mongoDBQueryHolder.getProjection();
        Document sort = mongoDBQueryHolder.getSort();

        List<String> cols = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : columns.entrySet()) {
            if (entry.getValue() instanceof Integer) {
                int isShow = (Integer) entry.getValue();
                if (isShow == 1) {
                    cols.add(entry.getKey());
                }
            }
        }
        System.out.println("query:" + query);
        System.out.println("collection:" + collection);
        System.out.println("projection:" + projection);
        System.out.println("columns:" + columns);
        System.out.println("cols:" + cols);
        System.out.println("sort:" + sort);
    }

    @Test
    public void testQueryMongoDB() throws Exception {
        /** 使用mongosh 向mongo插入一条数据
             use test;
             db.numbers.insertOne({
             id: 1,
             bigint1: NumberLong("1234567890123456789"),  // 字符串避免精度丢失
             bigint2: NumberLong(9876543210987654321),     // 直接传入数字（注意可能丢失精度）
             num1: 1234567890,    // 自动存为 Int32（如果数值较小） 无精度丢失风险 查询出来不会带科学记数法
             num2: 123456789012345,   // 15位整数  自动存为 Double 风险：Double 是浮点数，虽然能精确表示部分整数，但超出 2^53 后可能丢失精度  查询结果带科学记数法
             num3: 1234567890123456789 //19位整数 风险：精度丢失，末尾数字可能被截断  查询结果带科学记数法
             });
             db.numbers.findOne({ id: 1 });
         */
        String mongoUrl = "mongodb://mongo:mongo@localhost:27017/admin";
        String dbName = "test";
        String sql = "select bigint1,bigint2,num1,num2,num3 from numbers where num1 = 1234567890";
        String field_delim = "\t";
        Gson gson = new Gson();
        MongoSQLClient mongoSQLClient = new MongoSQLClient(mongoUrl);
        MongoResult mongoResult = mongoSQLClient.select(dbName, sql);
        List<String> columns = mongoResult.getColumns();
        System.out.println("Output Columns:" + columns);
        int columnCount = columns.size();
        StringBuilder sb = new StringBuilder();
        int rowCount = 0;

        //查找集合中的所有文档
        QueryResultIterator<Document> resultIterator = mongoResult.getIterator();
        while (resultIterator.hasNext()) {
            Document document = resultIterator.next();
            // log.info(document.toJson());
            for (int i = 0; i < columnCount; i++) {
                String k = columns.get(i);
                String v = "";
                if (document.containsKey(k)) {
                    Object o = document.get(k);
                    if (o != null) {
                        String stype =o.getClass().getCanonicalName();
                        if ("java.util.Date".equals(stype)) {
                            long vv = ((java.util.Date) o).getTime();
                            v = String.valueOf(vv);
                        } else {
                            JsonElement je = gson.toJsonTree(o);
                            if (je.isJsonArray() || je.isJsonObject()) {
                                v = je.toString();
                            } else {
                                v = je.getAsString();
                            }
                        }
                    }
                }
                if (i == 0) {
                    sb.append(v);
                } else {
                    sb.append(field_delim).append(v);
                }
            }
            String doc = sb.toString();
            System.out.println(doc);
            sb.delete(0, sb.length());
            rowCount++;
        }

        System.out.println("Total row count:" + rowCount);
    }


}
