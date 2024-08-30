package parquet.writer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe;
import org.apache.hadoop.hive.ql.io.parquet.timestamp.NanoTimeUtils;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.GroupFactory;
import org.apache.parquet.example.data.simple.NanoTime;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.ExampleParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;
import org.apache.parquet.schema.Types;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.apache.parquet.schema.LogicalTypeAnnotation.*;
import static org.apache.parquet.schema.LogicalTypeAnnotation.TimeUnit.*;
import static org.apache.parquet.schema.OriginalType.*;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.*;


/**
 * @author shmily
 * Description: write data to parquet file
 */
public class ParquetWriterDemo {

    private static final DateTimeFormatter dfDay = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int JULIAN_EPOCH_OFFSET_DAYS = 2440588;
    private static final long MILLIS_IN_DAY = TimeUnit.DAYS.toMillis(1);
    private static final long NANOS_PER_MILLISECOND = TimeUnit.MILLISECONDS.toNanos(1);

    /**
     *  Demo写入parquet文件  支持复杂数据类型
     -- 在Hive/Spark验证文件是否可正常读取
     CREATE TABLE parquet_table (
         stringVal STRING,
         intVal INT,
         bigintVal BIGINT,
         decimalVal DECIMAL(22,2),
         structVal STRUCT<
             stringValInStruct: STRING,
             intValInStruct: INT,
             decimalValInStruct: DECIMAL(22,2)
         >,
         mapVal MAP<STRING, STRING>,
         listVal ARRAY<STRING>
     )
     STORED AS PARQUET;
     -- 结论：Hive Spark中可正常读取
     * @throws IOException
     */
    public void writeComplexParquet(String parquetFilePath) throws IOException {
        // Schema
        MessageType schema = Types.buildMessage()
                .optional(INT32).named("int_col")
                .optional(INT64).named("bigint_col")
                .optional(FLOAT).named("float_col")
                .optional(DOUBLE).named("double_col")
                .optional(FIXED_LEN_BYTE_ARRAY).length(5).as(decimalType(2, 10)).named("decimal_col")
                .optional(BINARY).as(stringType()).named("string_col")
                .optional(BINARY).named("varchar_col")
                .optional(BINARY).named("char_col")
                .optional(BOOLEAN).named("boolean_col")
                .optional(BINARY).named("binary_col")
                .optional(INT32).as(dateType()).named("dt_col")
//                .optional(INT96).named("ts_col")  // TODO: 支持timestamp类型

                .optionalGroup()
                    .as(listType())
                        .repeatedGroup()
                        .optional(BINARY).as(stringType()).named("elem")
                        .named("bag")
                    .named("array_col")

                .optionalGroup()
                    .as(mapType())
                        .repeatedGroup()
                        .as(MAP_KEY_VALUE)
                        .required(BINARY).as(stringType()).named("key")
                        .optional(INT32).named("value")
                        .named("mapEntry")
                    .named("map_col")

                .optionalGroup()
                    .optional(INT32).named("id")
                    .optional(BINARY).as(stringType()).named("name")
//                    .optional(FIXED_LEN_BYTE_ARRAY).length(10).as(decimalType(2, 22)).named("decimalValInStruct")
                    .named("struct_col")

                .named("parquet_schema");

        System.out.println(schema.toString());

        // Writer initialization
        Path file = new Path(parquetFilePath);
        Configuration configuration = new Configuration();
        GroupFactory factory = new SimpleGroupFactory(schema);
        ParquetWriter<Group> writer = ExampleParquetWriter.builder(file)
                .withConf(configuration)
                .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
                .withType(schema)
                // SNAPPY 也支持,但ZSTD压缩比更高文件更小（ZSTD缺点是解压更耗CPU）
                .withCompressionCodec(CompressionCodecName.ZSTD)
                .build();

        // Write data: 1000 rows
        for (int i = 0; i < 1000; i++) {
            Group row = factory.newGroup()
                    .append("int_col", i)
                    .append("bigint_col", i * 1000L)
                    .append("float_col", i * 3.1415f)
                    .append("double_col", i * 3.1415926)
                    .append("decimal_col", decimalStrToBinary("3.14", 10, 2))
                    .append("string_col", String.format("string_%d", i))
                    .append("varchar_col", String.format("varchar_%d", i))
                    .append("char_col", String.format("c_%d", i))
                    .append("boolean_col", true)
                    .append("binary_col", String.format("binary_%d", i))
                    .append("dt_col", 19960 + i)
                    // TODO: 支持timestamp类型
//                    .append("ts_col", 19960 + i * 1000L)
                    ;

            // write array
            Group arr = row.addGroup("array_col");
            for (int j = 0; j < 3; j++) {
                Group bag = arr.addGroup("bag");
                bag.add("elem", String.format("elem_%d", i + j));
            }

            // write map
            Group map = row.addGroup("map_col");
            for (int j = 0; j < 3; j++) {
                // 写入entry
                Group mapEntry = map.addGroup("mapEntry");
                mapEntry.add("key", String.format("key_%d", j));
                mapEntry.add("value", j);
            }

            // write struct
            Group struct = row.addGroup("struct_col");
            struct.add("id", i);
            struct.add("name", String.format("name_%d", i));
//            struct.add("decimalValInStruct", decimalStrToBinary("3.14", 22, 2));

            writer.write(row);
        }

        // Writer close
        writer.close();
    }

    /**
     * Decimal类型数据写入
     * @param val 值，String类型，但数据为小数 如"3.14"
     * @param precision  精度（小数点后几位）
     * @param scale 规模
     * @return Parquet column binary
     */
    public static Binary decimalStrToBinary(String val, int precision, int scale) {
        HiveDecimal hiveDecimal = HiveDecimal.create(val);
        byte[] decimalBytes = hiveDecimal.bigIntegerBytesScaled(scale);
        // Estimated number of bytes needed.
        int precToBytes = ParquetHiveSerDe.PRECISION_TO_BYTE_COUNT[precision - 1];
        if (precToBytes == decimalBytes.length) {
            // No padding needed.
            return Binary.fromByteArray(decimalBytes);
        }
        byte[] tgt = new byte[precToBytes];
        if (hiveDecimal.signum() == -1) {
            // For negative number, initializing bits to 1
            for (int i = 0; i < precToBytes; i++) {
                tgt[i] |= 0xFF;
            }
        }
        System.arraycopy(decimalBytes, 0, tgt, precToBytes - decimalBytes.length, decimalBytes.length);
        return Binary.fromByteArray(tgt);
    }


    /**
     * Demo写入简单parquet文件 （不支持复杂数据结构map struct array）
     * @throws IOException
     */
    public void writerSimpleParquetDemo(String parquetFilePath) throws IOException {
        //required不能为null或丢失  optional允许空值和丢失
        String schemaString = "message schema { "
                + "required INT32 intValue; "
                + "required INT64 longValue; "
                + "required DOUBLE doubleValue; "
                + "required BINARY stringValue; "
                + "required BINARY binValue; "
                + "}";

        MessageType schema = MessageTypeParser.parseMessageType(schemaString);
        GroupFactory factory = new SimpleGroupFactory(schema);

        Path file = new Path(parquetFilePath);
        Configuration configuration = new Configuration();

        ParquetWriter<Group> writer = ExampleParquetWriter.builder(file)
                .withConf(configuration)
                .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
                .withType(schema)
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .build();

        // Write some data
        for (int i = 0; i < 1000; i++) {
            Group group = factory.newGroup()
                    .append("intValue", i)
                    .append("longValue", i*1000L)
                    .append("doubleValue", 3.1415926)
                    .append("stringValue", "haha")
                    .append("binValue", new String("bin_haha"));
            writer.write(group);
        }
        writer.close();
    }




}
