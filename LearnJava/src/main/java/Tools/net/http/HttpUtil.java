package Tools.net.http;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * http请求工具类
 */
public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private static final PoolingHttpClientConnectionManager connectionManager;
    private static final HttpClientBuilder httpBuilder;
    private static final RequestConfig requestConfig;

    // 最大连接数
    private static final int MAX_CONNECTION = 50;

    // 最大并发请求数
    private static final int DEFAULT_MAX_CONNECTION = 25;

    // 1秒不活动后验证连接
    private static final int VALIDATE_AFTER_INACTIVITY = 2000;

    static {
        //设置http的状态参数
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(2000)
                .build();

        connectionManager = new PoolingHttpClientConnectionManager(60, TimeUnit.SECONDS);
        connectionManager.setMaxTotal(MAX_CONNECTION);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTION);
        connectionManager.setValidateAfterInactivity(VALIDATE_AFTER_INACTIVITY);
        httpBuilder = HttpClients.custom();
        httpBuilder.setConnectionManager(connectionManager)
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                .setDefaultRequestConfig(RequestConfig.custom().setStaleConnectionCheckEnabled(true).build())
                .setRetryHandler((exception, executionCount, context) -> {
                    logger.info("start retry connection count:[{}], exception msg:[{}]", executionCount, Objects.nonNull(exception) ? exception.getMessage() : "unknown");
                    int retryTimes = 5;
                    if (executionCount > retryTimes) {
                        return false;
                    }
                    if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                        return true;
                    }
                    if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                        return false;
                    }
                    if (exception instanceof InterruptedIOException) {// 超时
                        return false;
                    }
                    if (exception instanceof UnknownHostException) {// 目标服务器不可达
                        return false;
                    }
                    if (exception instanceof ConnectTimeoutException) {// 连接被拒绝, 重试
                        return true;
                    }
                    if (exception instanceof SSLException) {// SSL握手异常
                        return false;
                    }
                    HttpClientContext clientContext = HttpClientContext
                            .adapt(context);
                    HttpRequest request = clientContext.getRequest();
                    // 如果请求是幂等的，就再次尝试
                    return !(request instanceof HttpEntityEnclosingRequest);
                });
    }

    /**
     * 获取http客户端连接
     * @return http客户端连接
     */
    public static CloseableHttpClient getConnection() {
        return httpBuilder.build();
    }

    /**
     * http post请求，利用http请求池 获取response对象
     *
     * @param url 请求url
     * @param paramsMap 请求参数
     * @return HttpResponse
     * @throws Exception 异常
     */
    public static HttpResponse httpPost(String url, Map<String, String> paramsMap) throws Exception {
        List<NameValuePair> params = new ArrayList<>();

        for (Map.Entry<String, String> e : paramsMap.entrySet()) {
            NameValuePair pair = new BasicNameValuePair(e.getKey(), e.getValue());
            params.add(pair);
        }

        HttpUriRequest postMethod = RequestBuilder.post().setUri(url)
                .addParameters(params.toArray(new NameValuePair[0]))
                .setConfig(requestConfig).build();

        return getConnection().execute(postMethod);
    }


    /**
     * http post请求，利用http请求池
     *
     * @param url     请求url
     * @param jsonStr json字符串
     * @return 请求结果
     * @throws Exception 异常
     */
    public static HttpResponse httpPost(String url, String jsonStr) throws Exception {
        HttpUriRequest postMethod = RequestBuilder.post().setUri(url)
                .setHeader("Content-Type", "application/json;charset=utf-8")
                .setHeader("Accept", "application/json")
                .setEntity(new StringEntity(jsonStr, StandardCharsets.UTF_8))
                .setConfig(requestConfig).build();
        return getConnection().execute(postMethod);
    }

    /**
     * http post请求，利用http请求池 获取response结果值
     *
     * @param url       请求url
     * @param paramsMap 请求参数 map
     * @return 请求结果
     * @throws Exception 异常
     */
    public static String httpPostResult(String url, Map<String, String> paramsMap) throws Exception {
        HttpResponse response = httpPost(url, paramsMap);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * http post请求，利用http请求池 获取response结果值
     *
     * @param url       请求url
     * @param jsonStr 请求参数 json string
     * @return 请求结果
     * @throws Exception 异常
     */
    public static String httpPostResult(String url, String jsonStr) throws Exception {
        HttpResponse response = httpPost(url, jsonStr);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * http get请求，利用http请求池 获取response
     *
     * @param url 请求url
     * @return HttpResponse
     * @throws Exception 异常
     */
    public static HttpResponse httpGet(String url) throws Exception {
        HttpUriRequest getMethod = RequestBuilder.get().setUri(url)
                .setConfig(requestConfig).build();

        return getConnection().execute(getMethod);
    }

    /**
     * http get请求，利用http请求池 获取response
     * @param url 请求url
     * @param paramsMap 请求参数
     * @return HttpResponse
     * @throws Exception 异常
     */
    public static HttpResponse httpGet(String url, Map<String, String> paramsMap) throws Exception {
        List<NameValuePair> params = new ArrayList<>();

        for (Map.Entry<String, String> e : paramsMap.entrySet()) {
            NameValuePair pair = new BasicNameValuePair(e.getKey(), e.getValue());
            params.add(pair);
        }

        HttpUriRequest getMethod = RequestBuilder.get().setUri(url)
                .addParameters(params.toArray(new NameValuePair[0]))
                .setConfig(requestConfig).build();

        return getConnection().execute(getMethod);
    }

    /**
     * http get请求，利用http请求池 获取结果值
     *
     * @param url 请求url
     * @return 结果值
     * @throws Exception 异常
     */
    public static String httpGetResult(String url) throws Exception {
        return EntityUtils.toString(httpGet(url).getEntity());
    }

    /**
     * http get请求，利用http请求池 获取结果值
     *
     * @param url 请求url
     * @param paramsMap 请求参数 map
     * @return 结果值
     * @throws Exception 异常
     */
    public static String httpGetResult(String url, Map<String, String> paramsMap) throws Exception {
        return EntityUtils.toString(httpGet(url, paramsMap).getEntity());
    }

    /**
     * http post 请求,每次创建请求客户端 返回结果值
     *
     * @param url    url
     * @param params 请求参数
     * @return 请求返回值
     */
    public static String httpPostNoPool(String url, Map<String, String> params) {
        CloseableHttpClient closeableHttpClient = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            if (params != null) {
                List<NameValuePair> form = new ArrayList<>();
                for (String name : params.keySet()) {
                    form.add(new BasicNameValuePair(name, params.get(name)));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(form, HTTP.UTF_8));
            }
            closeableHttpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = closeableHttpClient.execute(httpPost);
            HttpEntity entry = httpResponse.getEntity();
            return EntityUtils.toString(entry);
        } catch (Exception e) {
            logger.error("HttpUtil.httpPost failed!", e);
        } finally {
            if (null != closeableHttpClient) {
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    logger.error("closeableHttpClient.close failed!", e);
                }
            }
        }
        return null;
    }
}