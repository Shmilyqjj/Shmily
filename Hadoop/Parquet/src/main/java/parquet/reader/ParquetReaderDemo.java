package parquet.reader;

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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shmily
 * @Description: 读parquet文件
 * @CreateTime: 2024/7/15 下午10:04
 */

public class ParquetReaderDemo {
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
            List<Object> row = transRowFromLine(line, schema.getFields());
            System.out.println(gson.toJson(row));

//            String row = String.format("| %s | %s | %s | %s |",
//                    line.getString("stringVal", 0),
//                    line.getInteger("intVal", 0),
//                    line.getLong("bigintVal", 0),
//                    binaryToBigDecimal(line.getBinary("decimalVal", 0), 2)
//            );
//            System.out.println(row);



        }
        reader.close();
    }

    /**
     * 将parquet row group 按类型读数据
     * @param line rowGroup
     */
    private static List<Object> transRowFromLine(Group line, List<Type> fields) {
        List<Object> parsedData = new ArrayList<>();
        for (Type field : fields) {
            String fieldName = field.getName();
            //TODO: 根据类型解析并获取对应类型的数据 拼接row
            if (field.isPrimitive()) {
                // 基本类型
                parsedData.add(getPrimitiveValue(line, field));
            }else {
                // 复杂数据类型 (递归处理)
                GroupType fieldGroupType = field.asGroupType();
                List<Type> nestedFields = fieldGroupType.getFields();
                List<Object> nestedData = new ArrayList<>();
                int fieldCount = line.getFieldRepetitionCount(fieldName);
                for (int i = 0; i < fieldCount; i++) {
                    Group group = line.getGroup(fieldName, i);
                    nestedData.add(transRowFromLine(group, nestedFields));
                }
                parsedData.add(nestedData);
            }
        }
        return parsedData;
    }


    /**
     * 获取基本类型数据值
     * @param line parquet row group
     * @param field parquet field schema
     * @return value of this field
     */
    private static Object getPrimitiveValue(Group line, Type field) {
        String fieldName = field.getName();
        if (field.isPrimitive()) {
            // 基本类型
            PrimitiveType primitiveType = field.asPrimitiveType();
            switch (primitiveType.getPrimitiveTypeName()) {
                case INT32:
                    return line.getInteger(fieldName, 0);
                case INT64:
                    return line.getLong(fieldName, 0);
                case FLOAT:
                    return line.getFloat(fieldName, 0);
                case DOUBLE:
                    return line.getDouble(fieldName, 0);
                case BINARY:
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
                            return line.getBinary(fieldName, 0).toStringUsingUTF8();
                        case BSON:
                            return line.getBinary(fieldName, 0).getBytes();
                    }
                    break;

            }
        }
        throw new NonPrimitiveTypeException("not support complex type:" + field.getOriginalType());
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
        BigDecimal decimal = new BigDecimal(new java.math.BigInteger(bytes), scale);
        return decimal;
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
}
