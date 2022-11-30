#!/bin/bash

# nohup sh start-iceberg-manager.sh > manager.log 2>&1 &

export SPARK_HOME=/usr/local/complat/spark

/usr/local/complat/spark/bin/spark-submit \
--master yarn \
--driver-memory 6G \
--executor-memory 8G \
--executor-cores 1 \
--num-executors 20 \
--conf spark.driver.host=xx.xx.xx.xx \
--class com.xldw.iceberg.table.maintenance.IcebergTableMaintenance \
/usr/local/complat/iceberg-table-manager/iceberg-spark-1.0.jar