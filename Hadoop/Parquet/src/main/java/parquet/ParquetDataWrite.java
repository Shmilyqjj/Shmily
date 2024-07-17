package parquet;

import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.common.type.Timestamp;
import org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe;
import org.apache.hadoop.hive.ql.io.parquet.timestamp.NanoTimeUtils;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.io.api.Binary;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author shmily
 */
public class ParquetDataWrite {

    public static Boolean booleanDataWriter(Boolean val) {
        return val;
    }

    public static Integer byteDataWriter(byte val) {
        return new Integer(val);

    }

    public static Integer shortDataWriter(Short val) {
        return new Integer(val);
    }

    public static Integer intWriter(Integer val) {
        return val;
    }

    public static Long longWriter(Long val) {
        return val;
    }

    public static Float floatWriter(Float val) {
        return val;
    }

    public static Double doubleDataWriter(Double val) {
        return val;
    }

    public static Binary stringWriter(String val) {
        return Binary.fromString(val);
    }

    public static Binary varcharWriter(String val) {
        return Binary.fromString(val);
    }

    /**
     * 将byte[]数据转为Binary，用于写入
     */
    public static Binary binaryWrite(byte[] bytes) {
        return Binary.fromByteArray(bytes);
    }

    /**
     * 将时间戳Timestamp转为Binary，用于写入
     */
    public static Binary timestampWrite(Timestamp ts) {
        return NanoTimeUtils.getNanoTime(ts, false).toBinary();
    }

    /**
     * 将字符串Decimal数据转为Binary，用于写入使用
     *
     * @param val   数据值
     * @param prec  定义Decimal中的数据长度
     * @param scale 定义Decimal中小数点后面位数
     */
    public static Binary decimalWrite(String val, int prec, int scale) {
        HiveDecimal hiveDecimal = HiveDecimal.create(val);
        byte[] decimalBytes = hiveDecimal.bigIntegerBytesScaled(scale);

        // Estimated number of bytes needed.
        int precToBytes = ParquetHiveSerDe.PRECISION_TO_BYTE_COUNT[prec - 1];
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
     * 将Date数据类型转为Int
     */
    public static Integer dateWrite(Date date) {
        return Integer.valueOf(DateWritable.dateToDays(date));
    }

    /**
     * list 数据类型转为Group
     * @param group  主结构体
     * @param index 为当前数据在结构体中的位置，也可以传入字段名称
     * @param values  数组中的值，这里String只是示例，具体根据List立民安数据类型写入
     * @return
     */
    public static Group listWrite(Group group, int index,List<String> values){
        Group listGroup = group.addGroup(index);
        for(String v : values){
            Group bagGroup = listGroup.addGroup(0);
            bagGroup.add(0,v);
        }
        return group;
    }
    /**
     * map 数据类型转为Group
     * @param group  主结构体
     * @param index 为当前数据在结构体中的位置，也可以传入字段名称
     * @param values  map中Key和value只是示例，具体根据定义Map结构传入
     */
    public static Group mapWrite(Group group, int index, Map<String,String> values){
        Group mapGroup = group.addGroup(index);
        Iterator<String> iterable =  values.keySet().iterator();
        while (iterable.hasNext()){
            String key = iterable.next();
            String value = values.get(key);
            Group dataGroup =  mapGroup.addGroup(0);
            dataGroup.add("key",key);
            dataGroup.add("value",value);
        }
        return group;
    }

    /**
     * Struct 结构转为Group
     * @param group 主结构体
     * @param index 为当前数据在结构体中的位置，也可以传入字段名称
     * @param values 这里为示例，具体根据定义结构传入
     * @return
     */
    public static Group structWrite(Group group, int index,String[] values){
        Group structGroup = group.addGroup(index);
        for(int i = 0; i < values.length; i++){
            structGroup.add(i,values[i]);
        }
        return group;

    }

}
