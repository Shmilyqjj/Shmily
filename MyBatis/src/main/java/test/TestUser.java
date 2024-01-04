package test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import test.mapper.UserMapper;
import test.pojo.User;
import test.stream.StreamStringResultHandler;
import test.stream.StreamUserResultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        // 动态字段查询
        UserMapper userMapper = session.getMapper(UserMapper.class);
        Map<String, Object> dcR = userMapper.dynamicColumnsSelect("1011f11e-aa02-11ee-a4e7-0242ac110003", "name,age");
        dcR.entrySet().forEach(System.out::println);

        // 动态值查询
        List<String> userIds = new ArrayList<>();
        userIds.add("1011f11e-aa02-11ee-a4e7-0242ac110003");
        userIds.add("153354e5-aa02-11ee-a4e7-0242ac110003");
        userIds.add("18ea6778-aa02-11ee-a4e7-0242ac110003");
        userIds.add("19c9adaa-aa02-11ee-a4e7-0242ac110003");
        userIds.add("27758a5a-aa02-11ee-a4e7-0242ac110003");
        userIds.add("56baa962-aa01-11ee-a4e7-0242ac110003");
        List<Map<String, Object>> dvRs = userMapper.dynamicValuesSelect(userIds, "id,age");
        dvRs.forEach(dvR -> dvR.entrySet().forEach(System.out::println));

    }

}
