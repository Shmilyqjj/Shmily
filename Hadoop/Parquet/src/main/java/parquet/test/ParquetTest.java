package parquet.test;

import parquet.reader.ParquetReaderDemo;
import parquet.schema.ParquetDataSchema;
import parquet.writer.ParquetWriterDemo;
import java.io.IOException;


/**
 * @author shmily
 * @ Windows System Env： HADOOP_HOME=C:\Programing\Env\hadoop-3.2.1;LD_LIBRARY_PATH=C:\Programing\Env\hadoop-3.2.1\lib\native
 * @ Linux   System Env： HADOOP_HOME=/path/to/hadoop;LD_LIBRARY_PATH=$HADOOP_HOME/lib/native
 */
public class ParquetTest {
//    private static final String LOCAL_DIR = "E:\\";
    private static final String LOCAL_DIR = "/home/shmily/Desktop";
    public static void main(String[] args) throws IOException {
//        testSimpleTypeWrite(LOCAL_DIR + "/test.parquet");
//        testReadParquet(LOCAL_DIR + "/test.parquet");
//        testParquetSchema();
        testParquetWriter(LOCAL_DIR + "/test_complex.parquet");
        testParquetReader(LOCAL_DIR + "/test_complex.parquet");
    }


    public static void testSimpleTypeWrite(String filePath) throws IOException {
        ParquetWriterDemo pwd = new ParquetWriterDemo();
        pwd.writerSimpleParquetDemo(filePath);
    }

    public static void testReadParquet(String filePath) throws IOException {
        ParquetReaderDemo prd = new ParquetReaderDemo();
        prd.readSimpleParquetDemo(filePath);
    }

    public static void testParquetSchema() {
        ParquetDataSchema parquetDataSchema = new ParquetDataSchema();
        parquetDataSchema.printParquetSchemaStr();
    }

    public static void testParquetWriter(String filePath) throws IOException {
        ParquetWriterDemo pwd = new ParquetWriterDemo();
        pwd.writeComplexParquet(filePath);
    }

    public static void testParquetReader(String filePath) throws IOException {
        ParquetReaderDemo prd = new ParquetReaderDemo();
        prd.readComplexParquetFile(filePath);
    }

}