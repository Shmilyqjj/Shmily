package parquet.test;

import parquet.reader.ParquetReaderDemo;
import parquet.schema.ParquetDataSchema;
import parquet.writer.ParquetWriterDemo;

import java.io.File;
import java.io.IOException;


/**
 * @author shmily
 * @ Windows System Env： HADOOP_HOME=C:\Programing\Env\hadoop-3.2.1;LD_LIBRARY_PATH=C:\Programing\Env\hadoop-3.2.1\lib\native
 * @ Linux   System Env： HADOOP_HOME=/path/to/hadoop;LD_LIBRARY_PATH=$HADOOP_HOME/lib/native
 */
public class ParquetTest {
//    private static final String LOCAL_DIR = "E:\\";
    private static final String LOCAL_DIR = "/home/shmily/Desktop";
    private static final String PROJECT_DIR = "/home/shmily/Projects/MyProjects/JavaProjects/Shmily/Hadoop/Parquet";
    public static void main(String[] args) throws IOException {
//        testSimpleTypeWrite(LOCAL_DIR + "/test.parquet");
//        testReadParquet(LOCAL_DIR + "/test.parquet");
//        testParquetSchema();
        testParquetWriter(LOCAL_DIR + "/test_complex.parquet");
        testParquetReader(LOCAL_DIR + "/test_complex.parquet");
        // 清理
        cleanDataFile(LOCAL_DIR + "/test.parquet");
        cleanDataFile(LOCAL_DIR + "/test_complex.parquet");

        testParquetReader(PROJECT_DIR + "/ParquetFiles/spark_file.parquet");
        testParquetReader(PROJECT_DIR + "/ParquetFiles/hive_file.parquet");


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


    /**
     * 删除已创建的文件
     * @param filePath 文件路径
     * @throws IOException 文件IO异常
     */
    public static void cleanDataFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Deleted file: " + filePath);
            } else {
                System.out.println("Failed to delete the file: " + filePath);
            }
        }
    }

}