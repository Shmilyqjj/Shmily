#!/usr/bin/env python
# encoding: utf-8
"""
:Description: Send kafka
:Author: 佳境Shmily
:Create Time: 2021/12/30 21:00
:File: SendKafka
:Site: shmily-qjj.top
"""

from kafka import KafkaProducer
import time
from kafka.errors import kafka_errors


TOPIC = 'flink_sql_topic'


if __name__ == '__main__':
    producer = KafkaProducer(bootstrap_servers='192.168.1.101:9092,192.168.1.102:9092,192.168.1.104:9092')
    start_time = time.time()
    for i in range(0, 1000):
        msg = '{"id": "%s", name: "qjj", "ts": "%s"}' % (i, int(time.time()))
        print(msg)
        future = producer.send(TOPIC, msg.encode(), partition=0)
    # 将缓冲区的全部消息push到broker当中
    producer.flush()
    producer.close()
    time_cost = time.time() - start_time
    print('发送耗时 %s 秒' % time_cost)


