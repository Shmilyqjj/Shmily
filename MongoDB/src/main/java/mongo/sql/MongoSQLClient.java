package mongo.sql;

import com.github.vincentrussell.query.mongodb.sql.converter.MongoDBQueryHolder;
import com.github.vincentrussell.query.mongodb.sql.converter.ParseException;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryConverter;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryResultIterator;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import mongo.sql.model.MongoResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: sql query mongodb
 * CreateTime: 2025/8/8 12:06
 * Author Shmily
 */
public class MongoSQLClient {
    private static final Logger log = LoggerFactory.getLogger(MongoSQLClient.class);
    private static MongoClient mongoClient;

    public MongoSQLClient(String url) {
        mongoClient = MongoClients.create(url);
    }

    public void testConn() {
        mongoClient.listDatabases();
    }

    public MongoResult select(String dbName, String sql) throws Exception {
        QueryConverter queryConverter = (new QueryConverter.Builder()).sqlString(sql).build();
        MongoDBQueryHolder mongoDBQueryHolder = queryConverter.getMongoQuery();
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
        Object result = queryConverter.run(mongoDatabase);
        Document columns = mongoDBQueryHolder.getProjection();
        List<String> cols = new ArrayList();

        for(Map.Entry<String, Object> entry : columns.entrySet()) {
            if (entry.getValue() instanceof Integer) {
                int isShow = (Integer)entry.getValue();
                if (isShow == 1) {
                    cols.add((String)entry.getKey());
                }
            }
        }

        if (cols.size() == 0) {
            throw new Exception("columns size is 0");
        } else if (QueryResultIterator.class.isInstance(result)) {
            QueryResultIterator<Document> iterator = (QueryResultIterator)result;
            MongoResult mongoResult = new MongoResult();
            mongoResult.setColumns(cols);
            mongoResult.setIterator(iterator);
            return mongoResult;
        } else {
            throw new Exception("result is not QueryResultIterator<Document>");
        }
    }

    public void close() {
        mongoClient.close();
    }
}
