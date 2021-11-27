#!/usr/bin/env python
# encoding: utf-8
"""
:Description: 发Kafka
:Author: 佳境Shmily
:Create Time: 2021/11/26 21:51
:File: send_kafka
:Site: shmily-qjj.top
"""

from kafka import KafkaProducer
import time
import json

BOOTSTRAP_SERVERS = '192.168.1.101:9092,192.168.1.102:9092,192.168.1.103:9092,192.168.1.104:9092'
TOPIC = 'kudu_data_topic'

KUDU_TARGET_TABLE1 = "kafka2kudu_test1"
KUDU_TARGET_TABLE2 = "kafka2kudu_test2"

producer = KafkaProducer(bootstrap_servers=BOOTSTRAP_SERVERS)


# DDLs
def create_table():
    # 建Hash分区Kudu表1  以id字段为主键，分区类型为哈希分区，桶数为6
    create_table_msg1 = json.dumps({
        "op_type": "create_table",
        "kudu_table": KUDU_TARGET_TABLE1,
        "pk": "id",
        "partition_type": "hash(6)",
        "cols": "{'name':'string', 'age': 'int'}"
    }).encode()
    print(create_table_msg1)
    producer.send(TOPIC, create_table_msg1)

    # 建Range分区Kudu表2  以id字段为主键，分区类型为范围分区，id范围起始为0，每个分区范围大小为10，最后一个分区最大数据为100数据
    create_table_msg2 = json.dumps({
        "op_type": "create_table",
        "kudu_table": KUDU_TARGET_TABLE2,
        "pk": "id",
        "partition_type": "range(0,10,100)",
        "cols": "{'name':'string', 'age': 'int'}"
    }).encode()
    print(create_table_msg2)
    producer.send(TOPIC, create_table_msg2)


def delete_column(table, col_name):
    post_data = json.dumps({
        "op_type": "delete_column",
        "kudu_table": table,
        "column_name": "%s" % col_name
    }).encode()
    print('producer - %s' % post_data)
    producer.send(TOPIC, post_data)


def delete_table(table):
    post_data = json.dumps({
        "op_type": "delete_table",
        "kudu_table": table
    }).encode()
    print('producer - %s' % post_data)
    producer.send(TOPIC, post_data)


# DMLs
def insert_data():
    i = 0
    while True:
        i += 1
        post_data1 = json.dumps({
            "op_type": "insert_data",
            "kudu_table": KUDU_TARGET_TABLE1,
            "pk": "id",
            "id": i,
            "data": "{'name':'qjj', 'age':'23'}"
        }).encode()

        post_data2 = json.dumps({
            "op_type": "insert_data",
            "kudu_table": KUDU_TARGET_TABLE2,
            "pk": "id",
            "id": i,
            "data": "{'name':'qjj', 'age':'23'}"
        }).encode()
        print('producer - %s' % post_data1)
        producer.send(TOPIC, post_data1)
        print('producer - %s' % post_data2)
        producer.send(TOPIC, post_data2)
        if i >= 20:
            break
        time.sleep(1)


def update_data(pk_val, new_data={"name": "abc"}):
    post_data = json.dumps({
        "op_type": "update_data",
        "kudu_table": KUDU_TARGET_TABLE1,
        "pk": "id",
        "id": "%s" % pk_val,
        "data": "%s" % new_data,
    }).encode()
    print('producer - %s' % post_data)
    producer.send(TOPIC, post_data)


def delete_data(table, pk_val):
    post_data = json.dumps({
        "op_type": "delete_data",
        "kudu_table": table,
        "pk": "id",
        "id": "%s" % pk_val,
    }).encode()
    print('producer - %s' % post_data)
    producer.send(TOPIC, post_data)


def test_other_msg():
    # 测试多余参数的情况
    post_data = json.dumps({
        "op_type": "delete_data",
        "kudu_table": KUDU_TARGET_TABLE1,
        "aaa": "id",
        "bbb": "l1",
        "ccc": "l1"
    }).encode()
    print('producer - %s' % post_data)
    producer.send(TOPIC, post_data)

    # 测试缺少关键参数的情况
    post_data = json.dumps({
        # "op_type": "delete_data",
        "kudu_table": KUDU_TARGET_TABLE1
    }).encode()
    print('producer - %s' % post_data)
    producer.send(TOPIC, post_data)

    post_data1 = json.dumps({
        "op_type": "delete_data",
        # "kudu_table": KUDU_TARGET_TABLE1,
        "aaa": "id",
        "bbb": "l1",
        "ccc": "l1"
    }).encode()
    print('producer - %s' % post_data1)
    producer.send(TOPIC, post_data1)


if __name__ == '__main__':
    # DDLs
    create_table()
    delete_column(KUDU_TARGET_TABLE1, 'age')
    delete_table(KUDU_TARGET_TABLE1)

    # DMLs
    delete_data(KUDU_TARGET_TABLE1, 10)
    insert_data()
    update_data(KUDU_TARGET_TABLE1, )
    # test_other_msg()


