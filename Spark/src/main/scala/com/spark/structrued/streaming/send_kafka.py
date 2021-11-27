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
TOPIC = 'test_topic'
producer = KafkaProducer(bootstrap_servers=BOOTSTRAP_SERVERS)


i = 0
while True:
    i += 1
    json_data = {
        "msg": "my kafka {}".format(i),
        "count": i
    }
    post_data = json.dumps(json_data).encode()

    producer.send(TOPIC, post_data)
    print('producer - {0}'.format(post_data))
    time.sleep(1)
