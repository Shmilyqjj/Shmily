package Tools.net.http;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * 使用HttpUtil
 */
public class HttpUtilUsage {
    public static void main(String[] args) throws Exception {
        // Get request (url)
        String getRes = HttpUtil.httpGetResult("http://localhost:8080/hello?name=qjj&&age=24");
        System.out.println(getRes);

        // Get request (param map)
        Map<String, String> getParamMap = new HashMap<>(2);
        getParamMap.put("name", "qjj");
        getParamMap.put("age", "24");
        String getRes1 = HttpUtil.httpGetResult("http://localhost:8080/hello", getParamMap);
        System.out.println(getRes1);

        // Get request (url, response)
        HttpResponse getRes2 = HttpUtil.httpGet("http://localhost:8080/hello?name=qjj&&age=24");
        System.out.println("status code: "+ getRes2.getStatusLine().getStatusCode() + "=== value:" + EntityUtils.toString(getRes2.getEntity()));

        // Post request (json string)
        String jsonStr = "{\"name\": \"qjj\", \"age\": \"24\"}";
        String postRes1 = HttpUtil.httpPostResult("http://localhost:8080/hello", jsonStr);
        System.out.println(postRes1);

        // Post request (param map)
        Map<String, String> paramMap = new HashMap<>(2);
        paramMap.put("name", "qjj");
        paramMap.put("age", "24");
        String postRes2 = HttpUtil.httpPostResult("http://localhost:8080/form", paramMap);
        System.out.println(postRes2);

        // Post request (no pool,param map)
        String postRes3 = HttpUtil.httpPostNoPool("http://localhost:8080/form", paramMap);
        System.out.println(postRes3);

        System.out.println("================================================");
        // Multi thread request get
        // 自己创建一个集合来保存Future存根并循环调用其返回结果的时候，主线程并不能保证首先获得的是最先完成任务的线程返回值。它只是按加入线程池的顺序返回。
        // 因为take方法是阻塞方法，后面的任务完成了，前面的任务却没有完成，主程序就那样等待在那儿，只到前面的完成了，它才知道原来后面的也完成了。
        ExecutorService exec= Executors.newFixedThreadPool(15);
        String[] strings = {"name=qjj&&age=24", "name=abc&&age=25", "name=def&&age=26", "name=xx&&age=22"
                , "name=g&&age=22", "name=f&&age=22", "name=gg&&age=2", "name=ff&&age=1", "name=xx&&age=22"
                , "name=h&&age=22", "name=e&&age=22", "name=hh&&age=2", "name=ee&&age=1", "name=xx&&age=22"
                , "name=i&&age=22", "name=d&&age=22", "name=ii&&age=2", "name=aa&&age=1", "name=xx&&age=22"
                , "name=xx&&age=22", "name=c&&age=22", "name=jj&&age=2", "name=bb&&age=1", "name=xx&&age=22"
                , "name=xx&&age=22", "name=b&&age=22", "name=ff&&age=2", "name=cc&&age=1", "name=xx&&age=22"
                , "name=xx&&age=22", "name=a&&age=22", "name=dd&&age=2", "name=dd&&age=1", "name=xx&&age=22"};

        BlockingQueue<Future<String>> queue = new LinkedBlockingQueue<>();
        for (String arg: strings) {
            Future<String> submit = exec.submit(() -> {
                String result = HttpUtil.httpGetResult("http://localhost:8080/hello?" + arg);
                Thread.sleep(2000);
                return Thread.currentThread().getName() + "====" + result;
            });
            queue.add(submit);
        }

        for (int i = 0; i < queue.size(); i++) {
            Future<String> future = queue.take();
            System.out.println("get结果:" + future.get());
        }

        exec.shutdown();

        // Multi thread request post
        // 使用CompletionService来维护处理线程不的返回结果时，主线程总是能够拿到最先完成的任务的返回值，而不管它们加入线程池的顺序。
        System.out.println("================================================");
        String[] jsonArr = {"{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}"
                , "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}"
                , "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}"
                , "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}"
                , "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}"
                , "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}"
                , "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}", "{\"name\": \"qjj\", \"age\": \"24\"}"};
        ExecutorService pool = Executors.newFixedThreadPool(8);
        CompletionService<String> cService = new ExecutorCompletionService<>(pool);

        for (String json: jsonArr) {
            cService.submit(() -> {
                String result = HttpUtil.httpPostResult("http://localhost:8080/hello", json);
                Thread.sleep(2000);
                return Thread.currentThread().getName() + "====" + result;
            });
        }

        for (int i = 0; i < jsonArr.length; i++) {
            Future<String> future = cService.take();
            System.out.println("post结果: " + future.get());
        }
        pool.shutdown();
    }
}
