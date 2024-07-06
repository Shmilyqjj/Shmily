package parquet.test;

import parquet.schema.ParquetDataSchema;
import parquet.writer.ParquetWriterDemo;

import java.io.IOException;

/**
 * @author shmily
 * @ Windows System Env： HADOOP_HOME=C:\Programing\Env\hadoop-3.2.1;LD_LIBRARY_PATH=C:\Programing\Env\hadoop-3.2.1\lib\native
 * @ Linux   System Env： HADOOP_HOME=/path/to/hadoop;LD_LIBRARY_PATH=$HADOOP_HOME/lib/native
 */
public class ParquetTest {
    public static void main(String[] args) throws IOException {
        ParquetWriterDemo prd = new ParquetWriterDemo();
        String parquetFile = "E:\\test.parquet";
        prd.writerSimpleParquetDemo(parquetFile);


        ParquetDataSchema parquetDataSchema = new ParquetDataSchema();
        parquetDataSchema.printParquetSchemaStr();
    }
}