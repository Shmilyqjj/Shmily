package parquet.writer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.GroupFactory;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.ExampleParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;

import java.io.IOException;


/**
 * @author shmily
 * Description: write data to parquet file
 */
public class ParquetWriterDemo {

    /**
     * Demo写入简单parquet文件 （不支持复杂数据结构map struct array）
     * @throws IOException
     */
    public void writerSimpleParquetDemo(String parquetFilePath) throws IOException {
        String schemaString = "message schema { "
                + "required INT32 id; "
                + "required BINARY name; "
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
                    .append("id", i)
                    .append("name", "name_" + i);
            writer.write(group);
        }
        writer.close();
    }

    /**
     *  Demo写入parquet文件  支持复杂数据类型
     * @throws IOException
     */
    public void writeParquet() {


    }





}
