package flink.study.streaming.sql

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.types.Row
import org.apache.flink.streaming.api.scala._

/**
 * :Description: 使用flink sql将kafka数据实时写入mysql
 * :Author: 佳境Shmily
 * :Create Time: 2021/12/30 20:13
 * :Site: shmily-qjj.top
 *
 * kafka-topics --create  --zookeeper 192.168.1.101:2181 --replication-factor 3 --partitions 1 --topic flink_topic1
 * kafka-topics --create  --zookeeper 192.168.1.101:2181 --replication-factor 3 --partitions 1 --topic flink_topic2
 */
object Kafka2MySQL {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)
    // 设置flink的执行并行度
    env.setParallelism(1)
    env.enableCheckpointing(500)


    // 创建kafka数据源表
    val createKafkaSourceTable: String =
      """
         CREATE TABLE kafka_source (
                `id` BIGINT,
                `name` STRING,
                `$offset` BIGINT,
                `ts` STRING
            ) WITH (
                'connector' = 'kafka',
                'topic' = 'flink_sql_topic',
                'properties.bootstrap.servers' = 'cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092',
                'properties.group.id' = 'kafka2mysql',
                'scan.startup.mode' = 'earliest-offset',
                'format' = 'json',
                'json.fail-on-missing-field' = 'false',
                'json.ignore-parse-errors' = 'true'
            )
        """
    tableEnv.executeSql(createKafkaSourceTable)

    val selectSQL: String =
      """
        |select
        |   `id`,
        |   `name`,
        |   `$offset`,
        |   `ts` w
        |   from kafka_source
        |""".stripMargin
    val rs:Table = tableEnv.sqlQuery(selectSQL)
    rs.printSchema()


    val ds:DataStream[Row] = tableEnv.toAppendStream[Row](rs)

    ds.print.setParallelism(1)


    env.execute("Kafka2MySQL")
  }
}
