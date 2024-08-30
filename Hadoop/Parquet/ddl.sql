-- Hive DDL for Parquet table
CREATE TABLE IF NOT EXISTS test.parquet_data_types (
    int_col INT,
    bigint_col BIGINT,
    float_col FLOAT,
    double_col DOUBLE,
    decimal_col DECIMAL(10, 2),
    string_col STRING,
    varchar_col VARCHAR(50),
    char_col CHAR(10),
    boolean_col BOOLEAN,
    binary_col BINARY,
    dt_col DATE,
    ts_col TIMESTAMP,
    array_col ARRAY<STRING>,
    map_col MAP<STRING, INT>,
    struct_col STRUCT<id: INT, name: STRING>
)
PARTITIONED BY (ds STRING, engine STRING)
STORED AS PARQUET;


-- Insert data using spark-sql
spark-sql> INSERT overwrite table test.parquet_data_types PARTITION (ds='20240830', engine='spark')
VALUES
(
    1,
    9876543210,
    3.14,
    2.718281828,
    12345.67,
    'spark string',
    'spark varchar',
    'charval',
    TRUE,
    BINARY('spark_binarydata'),
    DATE '2024-08-30',
    TIMESTAMP '2024-08-30 12:34:56',
    ARRAY('element1', 'element2'),
    MAP('key1', 1, 'key2', 2),
    NAMED_STRUCT('id', 1, 'name', 'QJJ')
),
(
    2,
    9876543210,
    3.14,
    2.718281828,
    12345.67,
    'spark string',
    'spark varchar',
    'charval',
    TRUE,
    BINARY('spark_binarydata'),
    DATE '2024-08-30',
    TIMESTAMP '2024-08-30 01:23:45',
    ARRAY('element3', 'element4'),
    MAP('key3', 3, 'key4', 4),
    NAMED_STRUCT('id', 1, 'name', 'ABC')
);
-- 合并成一个文件
INSERT overwrite table test.parquet_data_types PARTITION (ds='20240830', engine='spark')
select
/*+ COALESCE(1) */
    int_col,
    bigint_col,
    float_col,
    double_col,
    decimal_col,
    string_col,
    varchar_col,
    char_col,
    boolean_col,
    binary_col,
    dt_col,
    ts_col,
    array_col,
    map_col,
    struct_col
from
   test.parquet_data_types
where ds='20240830' and engine='spark';



-- Insert data using hive
hive> INSERT INTO test.parquet_data_types PARTITION (ds='20240830', engine='hive')
      VALUES
      (
          1,
          9876543210,
          3.14,
          2.718281828,
          12345.67,
          'hive string',
          'hive varchar',
          'hive charval',
          TRUE,
          BINARY('hive_binarydata'),
          DATE '2024-08-30',
          TIMESTAMP '2024-08-30 12:34:56',
          ARRAY('element1', 'element2'),
          MAP('key1', 1, 'key2', 2),
          NAMED_STRUCT('id', 1, 'name', 'QJJ')
      ),
      (
          2,
          9876543210,
          3.14,
          2.718281828,
          12345.67,
          'spark string',
          'spark varchar',
          'spark charval',
          TRUE,
          BINARY('hive_binarydata'),
          DATE '2024-08-30',
          TIMESTAMP '2024-08-30 01:23:45',
          ARRAY('element3', 'element4'),
          MAP('key3', 3, 'key4', 4),
          NAMED_STRUCT('id', 1, 'name', 'ABC')
      );