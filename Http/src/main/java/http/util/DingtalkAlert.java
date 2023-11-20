package http.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
public class DingtalkAlert {
    private static final Logger logger = LoggerFactory.getLogger(DingtalkAlert.class);
    private static final OkHttpClient mClient;
    private static String url;

    //初始化客户端
    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10L, TimeUnit.SECONDS);
        builder.readTimeout(10L, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(200);
        dispatcher.setMaxRequests(200);
        builder.dispatcher(dispatcher);
        mClient = builder.build();
        try {
            url = getSign();
            logger.info("Ding alert is enabled.");
        } catch (Exception e) {
            logger.error("Failed to get sign.");
            e.printStackTrace();
        }
    }

    /**
     * 通用 POST 请求方法  依赖 OKhttp3
     * @param message 所要发送的消息
     * @return 发送状态回执
     */
    public static String send(String message) {

        JSONObject jsonObject = new JSONObject();
        //固定参数
        jsonObject.put("msgtype", "text");
        JSONObject content = new JSONObject();
        //此处message是你想要发送到钉钉的信息
        content.put("content", message);
        jsonObject.put("text", content);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), jsonObject.toJSONString());
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = mClient.newCall(request).execute();
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            logger.error("Failed to send message: {}", message);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取签名
     * @return 返回签名
     */
    private static String getSign() throws Exception {
        String baseUrl = "https://oapi.dingtalk.com/robot/send?access_token=";
        String token = "a67b2b224f91b2xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx3b8112266656";
        String secret = "SEC1cfd428xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx1e1c";
        long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return baseUrl + token + "&timestamp=" + timestamp + "&sign=" +
                URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
    }

    @TestOnly
    public static void main(String[] args) {
        String message = "Test msg";
        String res = DingtalkAlert.send(message);
        System.out.println(res);
    }


}

