package mongo.sql.model;

import com.github.vincentrussell.query.mongodb.sql.converter.QueryResultIterator;
import lombok.Data;
import org.bson.Document;

import java.io.Serializable;
import java.util.List;

@Data
public class MongoResult implements Serializable {
    private List<String> columns;
    private QueryResultIterator<Document> iterator;
}
