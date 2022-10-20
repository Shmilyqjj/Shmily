package Tools.net.http;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

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

    }


}
