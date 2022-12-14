package http.clients;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.Duration;


public class OkHttpClientDemo {

    public static void main(String[] args) throws IOException, InterruptedException {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                // 设置连接超时时间
                .connectTimeout(Duration.ofSeconds(30))
                // 设置读超时时间
                .readTimeout(Duration.ofSeconds(60))
                // 设置写超时时间
                .writeTimeout(Duration.ofSeconds(60))
                // 设置完整请求超时时间
                .callTimeout(Duration.ofSeconds(120))
                // 添加一个拦截器
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    return chain.proceed(request);
                })
                // 注册事件监听器
                .eventListener(new EventListener() {
                    @Override
                    public void callEnd(@NotNull Call call) {
                        System.out.println("----------callEnd--------");
                        super.callEnd(call);
                    }
                })
                .build();

        /** Get request */
        //1.构造 Request 对象
        Request request = new Request.Builder()
                // 标识为 GET 请求
                .get()
                // 设置请求路径
                .url("http://localhost:8080/hello?name=qjj&age=24")
                // 添加头信息
                .addHeader("Content-Type", "text/plain")
                .build();
        //2.将 Request 封装为 Call
        Call call = httpClient.newCall(request);
        //3.执行请求 (同步或异步)
        // 同步
        Response response = call.execute();
        System.out.println("Get同步请求 结果: " + response.body().string());
        // 异步
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                System.out.println("异步Get请求 " + call.request().url() + " 出现异常 " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String body = response.body().string();
//                System.out.println("异步Get请求 " + call.request().url() + " 的响应结果为 " + body);
//            }
//        });

        /** Post request */
        // 1.构造 JSON 数据
        // 创建 JSON 对象
        JSONObject json = new JSONObject();
        json.put("name", "qjj");
        json.put("age", "24");
        // 构造 Content-Type 头
        MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
        // 构造请求数据
        RequestBody requestBody = RequestBody.create(json.toJSONString(), mediaType);
        // 2.构造Request对象
        Request postRequest = new Request.Builder()
            // post 方法中传入 构造的对象
            .post(requestBody)
            .url("http://localhost:8080/hello")
            .build();
        // 3.将 Request 封装为 Call
        Call postCall = httpClient.newCall(postRequest);
        // 同步或异步执行
        postCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("异步Post请求 " + call.request().url() + " 出现异常 " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                System.out.println("异步Post请求 " + call.request().url() + " 的响应结果为 " + body);
            }
        });

        /** File Upload request */
        // 1.构造File
        File file = new File("/home/shmily/tools/scripts/notifies/sleep-notify.sh");
        // 2.使用 MultipartBody 构造 Request 对象
        RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "admin")//
                .addFormDataPart("password", "admin")//
                // 添加上传文件
                .addFormDataPart("file", "sleep-notify.sh",
                        RequestBody.create( file, MediaType.parse("image/png")))
                .build();
        // 3.构造Request对象
        Request uploadRequest = new Request.Builder()
                .post(multipartBody)
                .url("http://localhost:8080/file/upload")
                .build();
        // 4.构造 Call 对象，并发送同步请求
        Call uploadCall = httpClient.newCall(uploadRequest);
        Response uploadRes = uploadCall.execute();
        System.out.println("同步Post请求文件上传 结果: " + uploadRes.body().string());


    }
}
