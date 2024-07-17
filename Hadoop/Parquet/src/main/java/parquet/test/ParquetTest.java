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
    public static void main(String[] args) throws IOException {
//        testSimpleTypeWrite();
//        testReadParquet();
//        testParquetSchema();
        testParquetWriter();
        testParquetReader();
    }


    public static void testSimpleTypeWrite() throws IOException {
        ParquetWriterDemo pwd = new ParquetWriterDemo();
        // String parquetFile = "E:\\test.parquet";
        String parquetFile = "/home/shmily/Desktop/test.parquet";
        pwd.writerSimpleParquetDemo(parquetFile);
    }

    public static void testReadParquet() throws IOException {
        ParquetReaderDemo prd = new ParquetReaderDemo();
        prd.readSimpleParquetDemo("/home/shmily/Desktop/test.parquet");
    }

    public static void testParquetSchema() {
        ParquetDataSchema parquetDataSchema = new ParquetDataSchema();
        parquetDataSchema.printParquetSchemaStr();
    }

    public static void testParquetWriter() throws IOException {
        ParquetWriterDemo pwd = new ParquetWriterDemo();
        pwd.writeComplexParquet("/home/shmily/Desktop/test_complex.parquet");
    }

    public static void testParquetReader() throws IOException {
        ParquetReaderDemo prd = new ParquetReaderDemo();
        prd.readComplexParquetFile("/home/shmily/Desktop/test_complex.parquet");
    }

}