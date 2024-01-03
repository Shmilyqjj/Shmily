package test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import test.pojo.User;
import test.stream.StreamStringResultHandler;
import test.stream.StreamUserResultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2024/1/3 下午2:30
 * @Site: shmily-qjj.top
 */

public class TestUser {
    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml"; // resource目录下
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sqlSessionFactory.openSession();

        // 全量查询
        List<User> listUsers = session.selectList("listUsers");
        listUsers.forEach(x -> System.out.println(x.toString()));

        // 流式批量查询
        StreamStringResultHandler streamStringResultHandler = new StreamStringResultHandler();
        session.select("streamListUsers", streamStringResultHandler);
        streamStringResultHandler.end();

        // 流式一条一条查询
        StreamUserResultHandler streamUserResultHandler = new StreamUserResultHandler();
        session.select("streamListUserInfo", streamUserResultHandler);
    }
}
