package test.stream;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import test.pojo.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 佳境Shmily
 * @Description: 流式读取User对象数据 处理
 * @CreateTime: 2024/1/3 下午2:39
 * @Site: shmily-qjj.top
 */

public class StreamUserResultHandler implements ResultHandler<User> {
    public void handleResult(ResultContext<? extends User> resultContext) {
        // 这里获取流式查询每次返回的单条结果
        User user = resultContext.getResultObject();
        // 单条消息做处理
        System.out.println(user.toString());
    }
}