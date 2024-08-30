package parquet.reader;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.schema.*;
import parquet.exceptions.NonPrimitiveTypeException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Shmily
 * @Description: 读parquet文件
 * @CreateTime: 2024/7/15 下午10:04
 */

public class ParquetReaderDemo {

    private static final int JULIAN_EPOCH_OFFSET_DAYS = 2440588;
    private static final long MILLIS_IN_DAY = TimeUnit.DAYS.toMillis(1);
    private static final long NANOS_PER_MILLISECOND = TimeUnit.MILLISECONDS.toNanos(1);
    private static final DateTimeFormatter dfDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public void readComplexParquetFile(String path) throws IOException {
        // 获取Schema
        ParquetMetadata readFooter = ParquetFileReader.readFooter(new Configuration(), new Path(path));
        MessageType schema = readFooter.getFileMetaData().getSchema();

        // 初始化reader
        GroupReadSupport readSupport = new GroupReadSupport();
        ParquetReader.Builder<Group> builder = ParquetReader.builder(readSupport, new Path(path));
        ParquetReader<Group> reader = builder.build();

        // 读取数据
        Gson gson = new GsonBuilder().create();
        Group line;
        while ((line = reader.read()) != null) {
//            List<Object> row = transRowFromLine(line, schema.getFields());
            Map<String, Object> row = transRowToMapFromLine(line, schema.getFields());
            System.out.println(gson.toJson(row));
        }
        reader.close();
    }

    /**
     * 将parquet row group 一行数据 按类型读数据
     * @param line rowGroup
     * @param fields schema列表
     */
    private static List<Object> transRowFromLine(Group line, List<Type> fields) {
        List<Object> parsedData = new ArrayList<>(fields.size());
        for (Type field : fields) {
            parsedData.add(getFieldValue(line, field));
        }
        return parsedData;
    }

    /**
     * 将parquet row group 一行数据 按类型读数据 （transRowFromLine的带字段名版本）
     * @param line rowGroup
     * @param fields schema列表
     */
    private static Map<String, Object> transRowToMapFromLine(Group line, List<Type> fields) {
        Map<String, Object> parsedData = new LinkedHashMap<>(fields.size());
        for (Type field : fields) {
            parsedData.put(field.getName(), getFieldValue(line, field));
        }
        return parsedData;
    }

    /**
     * 输出List类型字段值
     * @param line rowGroup
     * @param field schema
     * @return List<Object>
     */
    private static List<Object> getListValue(Group line, Type field) {
        List<Object> listData = new ArrayList<>();
        int fieldCount = line.getFieldRepetitionCount(field.getName());
        for (int i = 0; i < fieldCount; i++) {
            Group group = line.getGroup(field.getName(), i);
            listData.addAll(transRowFromLine(group, field.asGroupType().getFields()));
        }
        return listData;
    }

    /**
     * 输出Struct类型字段值
     * @param line rowGroup
     * @param fields schema列表
     * @return List<Object>
     */
    private static Map<String, Object> getStructValue(Group line, List<Type> fields) {
        Map<String, Object> structData = new LinkedHashMap<>(fields.size());
        for (Type field : fields) {
            structData.put(field.getName(), getFieldValue(line, field));
        }
        return structData;
    }

    /**
     * 输出Map类型字段值
     * @param line rowGroup
     * @param fields schema列表
     * @return List<Object>
     */
    private static Map<String, Object> getMapValue(Group line, List<Type> fields) {
        Map<String, Object> mapData = new LinkedHashMap<>(fields.size());
        // field 为 MAP_KEY_VALUE 类型 是repeatedGroup
        Type field = fields.get(0);
        String mapEntryFieldName = field.getName();
        GroupType fieldGroupType = field.asGroupType();
        List<Type> mapKeyValueFields = fieldGroupType.getFields();
        int fieldCount = line.getFieldRepetitionCount(mapEntryFieldName);
        for (int i = 0; i < fieldCount; i++) {
            Group group = line.getGroup(mapEntryFieldName, i);
            mapData.put(
                    getFieldValue(group, mapKeyValueFields.get(0)).toString(),
                    getFieldValue(group, mapKeyValueFields.get(1)));
        }
        return mapData;
    }

    /**
     * 获取字段值
     * @param line rowGroup
     * @param field schema
     * @return Object 数据
     */
    private static Object getFieldValue(Group line, Type field) {
        String fieldName = field.getName();
        if (field.isPrimitive()) {
            return getPrimitiveValue(line, field);
        }else {
            GroupType fieldGroupType = field.asGroupType();
            List<Type> nestedFields = fieldGroupType.getFields();
            int fieldCount = line.getFieldRepetitionCount(fieldName);
            if (field.getLogicalTypeAnnotation() == LogicalTypeAnnotation.listType()) {
                // list类型
                List<Object> listData = new ArrayList<>();
                for (int i = 0; i < fieldCount; i++) {
                    Group group = line.getGroup(fieldName, i);
                    listData.addAll(getListValue(group, nestedFields.get(0)));
                }
                return listData;
            } else if (field.getLogicalTypeAnnotation() == LogicalTypeAnnotation.mapType()) {
                // map类型
                Map<String, Object> mapData = new LinkedHashMap<>(fieldCount);
                Group repGroup = line.asGroup();
                for (int i = 0; i < fieldCount; i++) {
                    Group group = repGroup.getGroup(fieldName, i);
                    mapData.putAll(getMapValue(group, nestedFields));
                }
                return mapData;
            } else if (fieldCount >= 1 && field.getLogicalTypeAnnotation() == null) {
                // struct类型
                Map<String, Object> structData = new LinkedHashMap<>(fieldCount);
                for (int i = 0; i < fieldCount; i++) {
                    Group group = line.getGroup(fieldName, i);
                    structData.putAll(getStructValue(group, nestedFields));
                }
                return structData;
            } else {
                // TODO: 如果遗漏情况或其他数据类型 在这里补充
                throw new NonPrimitiveTypeException("Unresolved data type, field: " + field);
            }
        }

    }


    /**
     * 获取基本类型数据值
     * @param line parquet row group
     * @param field parquet field schema
     * @return value of this field
     */
    private static Object getPrimitiveValue(Group line, Type field) {
        String fieldName = field.getName();
        // 基本类型
        PrimitiveType primitiveType = field.asPrimitiveType();
//            field.getRepetition()
        switch (primitiveType.getPrimitiveTypeName()) {
            case INT32:
                // INT32 可以是INT也可以是DATE
                if (field.getOriginalType() == OriginalType.DATE) {
                    // Date类型格式化
                    int days = line.getInteger(fieldName, 0);
                    return LocalDate.ofEpochDay(days).format(dfDay);
                }
                return line.getInteger(fieldName, 0);
            case INT64:
                // INT64 是Long类型
                return line.getLong(fieldName, 0);
            case INT96:
                // INT96 是Timestamp类型
                return binaryToLongTimestamp(line.getInt96(fieldName, 0));
            case FLOAT:
                return line.getFloat(fieldName, 0);
            case DOUBLE:
                return line.getDouble(fieldName, 0);
            case BINARY:
                // BINARY 对应 string char varchar binary
                return line.getBinary(fieldName, 0).toStringUsingUTF8();
            case BOOLEAN:
                return line.getBoolean(fieldName, 0);
            case FIXED_LEN_BYTE_ARRAY:
                switch (field.getOriginalType()) {
                    case DECIMAL:
                        return binaryToBigDecimal(
                                line.getBinary(fieldName, 0),
                                primitiveType.getDecimalMetadata().getScale()
                        );
                    case UTF8:
                    case ENUM:
                    case JSON:
                    case BSON:
                    default:
                        return line.getBinary(fieldName, 0).toStringUsingUTF8();
                }
            default:
                throw new NonPrimitiveTypeException("Not a primitive type, field: " + fieldName);

        }
    }


    /**
     * parquet binary 转 decimal
     * @param binary getBinary result
     * @param scale 精度(小数点后几位)
     * @return java BigDecimal
     */
    private static BigDecimal binaryToBigDecimal(Binary binary, int scale) {
        ByteBuffer buffer = ByteBuffer.wrap(binary.getBytes()).order(ByteOrder.BIG_ENDIAN);
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new BigDecimal(new java.math.BigInteger(bytes), scale);
    }


    /**
     * 简单的读取Parquet文件 （Demo 不支持复杂数据类型）
     * @param path str abs path
     * @throws IOException ex
     */
    public void readSimpleParquetDemo(String path) throws IOException {
        GroupReadSupport readSupport = new GroupReadSupport();
        ParquetReader.Builder<Group> builder = ParquetReader.builder(readSupport, new Path(path));
        ParquetReader<Group> reader = builder.build();
        Group line;
        while ((line = reader.read()) != null) {
            System.out.printf("| %s | %s | %s | %s | %s |%n",
                    line.getInteger("intValue",0),
                    line.getLong("longValue",0),
                    line.getDouble("doubleValue",0),
                    line.getString("stringValue",0),
                    new String(line.getBinary("binValue",0).getBytes())
            );
        }
        reader.close();
    }


    public static long binaryToLongTimestamp(Binary timestampBinary)
    {
        if (timestampBinary.length() != 12) {
            return 0;
        }
        byte[] bytes = timestampBinary.getBytes();
        long timeOfDayNanos = Longs.fromBytes(bytes[7], bytes[6], bytes[5], bytes[4], bytes[3], bytes[2], bytes[1], bytes[0]);
        int julianDay = Ints.fromBytes(bytes[11], bytes[10], bytes[9], bytes[8]);
        return (julianDay - JULIAN_EPOCH_OFFSET_DAYS) * MILLIS_IN_DAY + (timeOfDayNanos / NANOS_PER_MILLISECOND);
    }


}
