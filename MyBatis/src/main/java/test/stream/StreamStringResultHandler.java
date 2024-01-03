package test.stream;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 佳境Shmily
 * @Description: 流式读取String数据 处理
 * @CreateTime: 2024/1/3 下午2:39
 * @Site: shmily-qjj.top
 */

public class StreamStringResultHandler implements ResultHandler<String> {
    // 这是每批处理的大小
    private final static int BATCH_SIZE = 10;
    private int size;
    // 存储每批数据的临时容器
    private final Set<String> strList = new HashSet<>();

    public void handleResult(ResultContext<? extends String> resultContext) {
        // 这里获取流式查询每次返回的单条结果
        String str = resultContext.getResultObject();
        // 你可以看自己的项目需要分批进行处理或者单个处理，这里以分批处理为例
        strList.add(str);
        size++;
        if (size == BATCH_SIZE) {
            handle();
        }
    }

    private void handle() {
        try {
            // 在这里可以对你获取到的批量结果数据进行需要的业务处理
            System.out.println("******One batch done******");
            strList.forEach(System.out::println);
        } finally {
            // 处理完每批数据后后将临时清空
            size = 0;
            strList.clear();
        }
    }

    // 这个方法给外面调用，用来完成最后一批数据处理
    public void end(){
        handle();// 处理最后一批不到BATCH_SIZE的数据
    }
}