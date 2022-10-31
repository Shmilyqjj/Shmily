package flink.connectors.userdefined.http;


import flink.connectors.userdefined.http.util.HttpUtil;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.types.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

import static org.apache.flink.configuration.ConfigOptions.key;

//TODO: 完善HTTP Source (implements DynamicTableSourceFactory )
/**
 * Http Connector
 * 实现DynamicTableSourceFactory, DynamicTableSinkFactory接口以实现connector
 * DynamicTableSourceFactory和DynamicTableSinkFactory提供连接器特定的逻辑，用于将CatalogTable的元数据转换为DynamicTableSource和DynamicTableSink的实例。
 * 在大多数情况下，工厂模式的目的是验证参数选项
 * 参考文档 <a href="https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/dev/table/sourcessinks/">User-defined Sources & Sinks </a>
 */
public class HttpConnectorFactory implements DynamicTableSinkFactory {
    // 定义sink name
    public static final String IDENTIFIER = "http";

    private static final Logger LOG = LoggerFactory.getLogger(HttpConnectorFactory.class);

    // 请求url
    public static final ConfigOption<String> URL = key("url")
            .stringType()
            .noDefaultValue()
            .withDescription("Request url path");

    // 请求类型 GET\POST.....
    public static final ConfigOption<String> METHOD = key("method")
            .stringType()
            .noDefaultValue()
            .withDescription("Http request method");

    public static final ConfigOption<Integer> RETRY_INTERVAL_MS = key("retry.interval.ms")
            .intType()
            .defaultValue(3000)
            .withDescription("Http request failed retry interval.");

    public static final ConfigOption<Integer> RETRY_MAX_NUM = key("retry.max.num")
            .intType()
            .defaultValue(-1)
            .withDescription("Http request failed retry num.Set -1 to infinite retry times");

    public static final ConfigOption<String> RETRY_STRATEGY = key("retry.strategy")
            .stringType()
            .defaultValue("keep")
            .withDescription("Policy after maximum number of retries.Available options: 'ignore','exit','keep'.");

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        // 必要参数
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(URL);
        options.add(METHOD);
        return options;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        // 可选参数  不可以return null 否则报错
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(RETRY_INTERVAL_MS);
        options.add(RETRY_MAX_NUM);
        options.add(RETRY_STRATEGY);
        return options;
    }

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);
        final ReadableConfig tableOptions = helper.getOptions();
        final String url = tableOptions.get(URL);
        final String method = tableOptions.get(METHOD).toLowerCase();
        final int retryInterval = tableOptions.get(RETRY_INTERVAL_MS);
        final int retryMaxNum = tableOptions.get(RETRY_MAX_NUM);
        final String retryStrategy = tableOptions.get(RETRY_STRATEGY);

        if(!("get".equals(method) || "post".equals(method))){
            throw new RuntimeException("Method "+ method + " is not supported.Only support GET/POST method.");
        }
        // derive the produced data type (excluding computed columns) from the catalog table
        final DataType producedDataType = context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType();
        return new HttpDynamicTableSink(url, method, producedDataType, retryInterval, retryMaxNum, retryStrategy);

    }


    private static class HttpDynamicTableSink implements DynamicTableSink{

        private final String url;

        private final String method;

        private final DataType producedDataType;

        private final int retryInterval;
        private final int retryMaxNum;
        private final String retryStrategy;


        public HttpDynamicTableSink(String url,
                                    String method,
                                    DataType producedDataType,
                                    int retryInterval,
                                    int retryMaxNum,
                                    String retryStrategy) {
            this.url = url;
            this.method = method;
            this.producedDataType = producedDataType; // unused
            this.retryInterval = retryInterval;
            this.retryMaxNum = retryMaxNum;
            this.retryStrategy = retryStrategy;
        }

        @Override
        public ChangelogMode getChangelogMode(ChangelogMode changelogMode) {
            return changelogMode;
        }

        // 返回SinkRuntimeProvider实现类 即定义的sink具体实现逻辑的类
        @Override
        public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
            final SinkFunction<RowData> sinkFunction = new HttpSinkFunction(
                    url,
                    method,
                    retryInterval,
                    retryMaxNum,
                    retryStrategy);
            return SinkFunctionProvider.of(sinkFunction);
        }

        @Override
        public DynamicTableSink copy() {
            return new HttpDynamicTableSink(url, method, producedDataType, retryInterval, retryMaxNum, retryStrategy);
        }

        @Override
        public String asSummaryString() {
            return "Http table sink";
        }
    }


    private static class HttpSinkFunction extends RichSinkFunction<RowData> {
        private final String url;
        private final String method;

        private final int retryInterval;

        private final int retryMaxNum;

        private final String retryStrategy;

        public HttpSinkFunction(String url, String method, int retryInterval, int retryMaxNum, String retryStrategy){
            this.url = url;
            this.method = method;
            this.retryInterval = retryInterval;
            this.retryMaxNum = retryMaxNum;
            this.retryStrategy = retryStrategy;
        }

        @Override
        public void open(Configuration parameters) {
            // 初始化
        }

        @Override
        public void invoke(RowData rowdata) throws InterruptedException {
            int curRetryNum = 0;
            try {
                // 取第一个字段的值 作为http请求参数
                String args = rowdata.getString(0).toString();
                String res = httpRequest(args);
                LOG.debug("Http sink succeed. Response: " + res);
            } catch (Exception e) {
                LOG.error("Failed to sink http.", e);
                // 发送失败 持续重新建立连接并重新发消息 retry
                while (true){
                    curRetryNum ++;
                    try {
                        String args = rowdata.getString(0).toString();
                        String res = httpRequest(args);
                        LOG.info("Http sink retry succeed. Response: " + res);
                        break;
                    }catch (Exception e1){
                        if(curRetryNum > this.retryMaxNum){
                            switch (this.retryStrategy.toLowerCase()){
                                case "ignore":
                                    LOG.warn("Failed to sink http.And max retries reached.Data is ignored.", e1);
                                    throw new RuntimeException("Failed to sink http.And max retries reached.Data is ignored.");
                                case "exit":
                                    LOG.error("Failed to sink http.And max retries reached.Exit the progress.", e1);
                                    System.exit(100);
                                case "keep":
                                    LOG.warn("Failed to sink http.And max retries reached.Keep retrying.", e1);
                                    Thread.sleep(this.retryInterval);
                                    continue;
                                default:
                                    LOG.warn("Failed to sink http.And max retries reached.Keep retrying.(The value of retry.strategy is not supported.Default to keep.)", e1);
                                    Thread.sleep(this.retryInterval);
                            }
                        }else {
                            LOG.error("Failed to http sink.Current retry: " + curRetryNum, e1);
                            Thread.sleep(this.retryInterval);
                        }
                    }
                }
            }
        }

        // 请求HTTP 获取String 结果
        public String httpRequest(String args) throws Exception {
            LOG.debug("Http url: " + this.url + " method: " + this.method + " args:" + args);
            String result = null;
            switch (this.method) {
                case "get":
                    // TODO: 完善http get请求代码
                    // TODO: 完善http 其他类型请求代码
                    // TODO: 完善http 参数类型
//                    result = HttpUtil.httpGetResult(url, new HashMap<>());
                case "post":
                    result = HttpUtil.httpPostResult(this.url, args);
            }
            LOG.info("Http sink result: " + result);
            return result;
        }

        @Override
        public void close() {

        }

    }


}
