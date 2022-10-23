package flink.connectors.userdefined.socket;


import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.SourceFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.*;
import org.apache.flink.table.runtime.connector.sink.SinkRuntimeProviderContext;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.RowKind;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 佳境Shmily
 * @Description: flink user-defined socket sql connector
 * @CreateTime: 2022/10/22 12:07
 * @Site: shmily-qjj.top
 */


/**
 * 类路径写入META-INF/services/org.apache.flink.table.factories.Factory文件， Java 的服务提供者接口 (SPI)可发现SocketConnectorFactory类
 * source-step3. create factory class implements DynamicTableSourceFactory
 * sink-step1   create factory class implements DynamicTableSinkFactory
 * @author Shmily
 */
public class SocketConnectorFactory implements DynamicTableSourceFactory, DynamicTableSinkFactory {

    private static final String FACTORY_IDENTIFIER = "socket";

    public static final ConfigOption<String> HOST = ConfigOptions.key("host")
            .stringType()
            .noDefaultValue();

    public static final ConfigOption<Integer> PORT = ConfigOptions.key("port")
            .intType()
            .noDefaultValue();

    public static final ConfigOption<Integer> BYTE_DELIMITER = ConfigOptions.key("byte-delimiter")
            .intType()
            .defaultValue(10); // corresponds to '\n'

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        // 必要参数
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(HOST);
        options.add(PORT);
        options.add(FactoryUtil.FORMAT); // use pre-defined option for format
        return options;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        // 可选参数
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(BYTE_DELIMITER);
        return options;
    }

    /**
     * 返回 DynamicTableSource 对象 即Source实现类 用于在SELECT查询中读取
     * @param context Context
     * @return 实现了 ScanTableSource或LookupTableSource
     */
    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        // discover a suitable decoding format
        final DecodingFormat<DeserializationSchema<RowData>> decodingFormat = helper.discoverDecodingFormat(
                DeserializationFormatFactory.class,
                FactoryUtil.FORMAT);

        // validate all options
        helper.validate();
        final ReadableConfig options = helper.getOptions();
        final String hostname = options.get(HOST);
        final int port = options.get(PORT);
        final byte byteDelimiter = (byte) (int) options.get(BYTE_DELIMITER);

        // derive the produced data type (excluding computed columns) from the catalog table
        final DataType producedDataType = context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType();

        // create and return dynamic table source
        return new SocketDynamicTableSource(hostname, port, byteDelimiter, decodingFormat, producedDataType);
    }

    @Override
    public String factoryIdentifier() {
        return FACTORY_IDENTIFIER;
    }

    /**
     * 返回 DynamicTableSink 对象 即Sink实现类 用于在 INSERT INTO 语句中写入
     * @param context Context
     * @return DynamicTableSink
     */
    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        // discover a suitable decoding format
        final EncodingFormat<SerializationSchema<RowData>> encodingFormat = helper.discoverEncodingFormat(
                SerializationFormatFactory.class,
                FactoryUtil.FORMAT);

        // validate all options
        helper.validate();
        final ReadableConfig options = helper.getOptions();
        final String hostname = options.get(HOST);
        final int port = options.get(PORT);
        final byte byteDelimiter = (byte) (int) options.get(BYTE_DELIMITER);

        // derive the produced data type (excluding computed columns) from the catalog table
        final DataType producedDataType = context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType();
        return new SocketDynamicTableSink(hostname, port, byteDelimiter, encodingFormat, producedDataType);
    }

    /**
     * source-step2. implements DynamicTableSource (include ScanTableSource and LookupTableSource)
     */
    private static class SocketDynamicTableSource implements ScanTableSource {

        private final String hostname;

        private final int port;

        private final byte byteDelimiter;

        private final DecodingFormat<DeserializationSchema<RowData>> decodingFormat;

        private final DataType producedDataType;

        public SocketDynamicTableSource(String hostname,
                                        int port,
                                        byte byteDelimiter,
                                        DecodingFormat<DeserializationSchema<RowData>> decodingFormat,
                                        DataType producedDataType) {
            this.hostname = hostname;
            this.port = port;
            this.byteDelimiter = byteDelimiter;
            this.decodingFormat = decodingFormat;
            this.producedDataType = producedDataType;
        }
        @Override
        public ChangelogMode getChangelogMode() {
            // define that this format can produce INSERT and DELETE rows
            return ChangelogMode.newBuilder()
                    .addContainedKind(RowKind.INSERT)
                    .build();
        }

        @Override
        public ScanRuntimeProvider getScanRuntimeProvider(ScanContext scanContext) {

            // 实例化 SourceFunction 和 DeserializationSchema
            final DeserializationSchema<RowData> deserializer = decodingFormat.createRuntimeDecoder(
                    scanContext,
                    producedDataType);
            final SourceFunction<RowData> sourceFunction = new SocketSourceFunction(
                    hostname,
                    port,
                    byteDelimiter,
                    deserializer);
            return SourceFunctionProvider.of(sourceFunction, false);
        }

        @Override
        public DynamicTableSource copy() {
            return new SocketDynamicTableSource(hostname, port, byteDelimiter, decodingFormat, producedDataType);
        }

        @Override
        public String asSummaryString() {
            return "Socket table source";
        }
    }

    /**
     *  source-step1.Create Socket Source Function
     *  extends RichSourceFunction<RowData> implements ResultTypeQueryable<RowData>
     */
    private static class SocketSourceFunction extends RichSourceFunction<RowData> implements ResultTypeQueryable<RowData> {
        private final String hostname;
        private final int port;
        private final byte byteDelimiter;
        private final DeserializationSchema<RowData> deserializer;

        private volatile boolean isRunning = true;
        private Socket currentSocket;


        public SocketSourceFunction(String hostname, int port, byte byteDelimiter, DeserializationSchema<RowData> deserializer){
            this.hostname = hostname;
            this.port = port;
            this.byteDelimiter = byteDelimiter;
            this.deserializer = deserializer;
        }

        @Override
        public TypeInformation<RowData> getProducedType() {
            return deserializer.getProducedType();
        }

        @Override
        public void run(SourceContext<RowData> sourceContext) throws Exception {
            while (isRunning) {
                // open and consume from socket
                try (final Socket socket = new Socket()) {
                    currentSocket = socket;
                    socket.connect(new InetSocketAddress(hostname, port), 0);
                    try (InputStream stream = socket.getInputStream()) {
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        int b;
                        while ((b = stream.read()) >= 0) {
                            // buffer until delimiter
                            if (b != byteDelimiter) {
                                buffer.write(b);
                            }
                            // decode and emit record
                            else {
                                sourceContext.collect(deserializer.deserialize(buffer.toByteArray()));
                                buffer.reset();
                            }
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace(); // print and continue
                }
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
            try {
                currentSocket.close();
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private static class SocketDynamicTableSink implements DynamicTableSink{

        private final String hostname;

        private final int port;

        private final byte byteDelimiter;

        private final EncodingFormat<SerializationSchema<RowData>> encodingFormat;

        private final DataType producedDataType;

        public SocketDynamicTableSink(String hostname,
                                        int port,
                                        byte byteDelimiter,
                                        EncodingFormat<SerializationSchema<RowData>> encodingFormat,
                                        DataType producedDataType) {
            this.hostname = hostname;
            this.port = port;
            this.byteDelimiter = byteDelimiter;
            this.encodingFormat = encodingFormat;
            this.producedDataType = producedDataType;
        }

        @Override
        public ChangelogMode getChangelogMode(ChangelogMode changelogMode) {
            return encodingFormat.getChangelogMode();
        }

        // 返回SinkRuntimeProvider实现类 即定义的socket sink具体实现逻辑的类
        @Override
        public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
            // 使用EncodingFormat创建序列化器
            final SerializationSchema<RowData> serializer = encodingFormat.createRuntimeEncoder(
                    context,
                    producedDataType);
            final SinkFunction<RowData> sinkFunction = new SocketSinkFunction(
                    hostname,
                    port,
                    byteDelimiter,
                    serializer);
            return SinkFunctionProvider.of(sinkFunction);
        }

        @Override
        public DynamicTableSink copy() {
            return new SocketDynamicTableSink(hostname, port, byteDelimiter, encodingFormat, producedDataType);
        }

        @Override
        public String asSummaryString() {
            return "Socket table sink";
        }
    }


    /**
     *  sink-step1.Create Socket Sink Function
     *  extends RichSinkFunction<RowData>
     */
    private static class SocketSinkFunction extends RichSinkFunction<RowData> {
        private final String hostname;
        private final int port;
        private final byte byteDelimiter;
        private final SerializationSchema<RowData> serializer;

        private Socket currentSocket;

        public SocketSinkFunction(String hostname, int port, byte byteDelimiter, SerializationSchema<RowData> serializer){
            this.hostname = hostname;
            this.port = port;
            this.byteDelimiter = byteDelimiter;
            this.serializer = serializer;
        }

        @Override
        public void open(Configuration parameters) {}


        @Override
        public void invoke(RowData rowdata) throws Exception {
                try (final Socket socket = new Socket()){
                    currentSocket = socket;
                    socket.connect(new InetSocketAddress(hostname, port), 3);
                    try (OutputStream outputStream = this.currentSocket.getOutputStream()){
                        System.out.println(rowdata.toString());
                        byte[] serialize = serializer.serialize(rowdata);
                        outputStream.write(serialize);
                        outputStream.write(byteDelimiter);
                        outputStream.flush();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

        }

        @Override
        public void close() {
            try {
                currentSocket.close();
            } catch (Throwable t) {
                // ignore
            }
        }

    }
}
