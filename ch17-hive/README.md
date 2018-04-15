## 1. 安装和配置hive(https://blog.csdn.net/wk51920/article/details/51731133)
```
tar -zxf apache-hive-2.3.2-bin.tar.gz
ln -s apache-hive-2.3.2-bin hive
vi ~/.zshrc

export HIVE_HOME=/Users/zzy/hive

export PATH=$PATH:$HIVE_HOME/bin

source ~/.zshrc

vi /Users/zzy/hive/conf/hive-site.xml
```
添加：
```
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>hive</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>hive</value>
    </property>
</configuration>
```

```
cd /Users/zzy/hive/conf
cp hive-env.sh.template hive-env.sh
```
修改：
```
HADOOP_HOME=/Users/zzy/hadoop
export HIVE_CONF_DIR=/Users/zzy/hive/conf
```

```
mysql -u root
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'zzy'@'%' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'hive'@'%' WITH GRANT OPTION;
CREATE USER 'hive'@'%' IDENTIFIED BY 'hive';
CREATE USER 'hive'@'localhost' IDENTIFIED BY 'hive';
GRANT ALL ON *.* TO 'hive'@'%' IDENTIFIED BY 'hive';
GRANT ALL ON *.* TO 'hive'@'localhost' IDENTIFIED BY 'hive';
FLUSH PRIVILEGES;
```

```
  hadoop fs -mkdir       /tmp
  hadoop fs -mkdir       /user/hive/warehouse
  hadoop fs -chmod g+w   /tmp
  hadoop fs -chmod g+w   /user/hive/warehouse

cp /Users/zzy/Downloads/mysql-connector-java-5.1.46/mysql-connector-java-5.1.46-bin.jar /Users/zzy/hive/lib
schematool -dbType mysql -initSchema
```

### 运行hive
1.启动集群
```
start-dfs.sh && start-yarn.sh
```
2.启动hive
```
hive

SHOW TABLES;
```

### 基本操作
1.运行脚本
```
hive -f script.q
```
2.行内嵌入命令
```
hive -e 'SELECT * FROM records'
```
3.生成一个单行表，用于测试查询
```
echo 'X' > /tmp/dummy.txt

hive -e "CREATE TABLE dummy (value STRING); \
LOAD DATA LOCAL INPATH '/tmp/dummy.txt' \
OVERWRITE INTO TABLE dummy"
```
4.不显示标准错误输出，使用'-S'选项
```
hive -S -e 'SELECT * FROM dummy'
```
5.使用'!'前缀，运行宿主操作系统的命令
```
!ls;
!cat /tmp/dummy.txt;
```
6.使用dfs命令来访问Hadoop文件系统
```
dfs -ls /user/zzy;
```

## 2. 示例(hive/max_temp.hive)
1.新建表
```
CREATE TABLE records (year STRING, temperature INT, quality INT)
ROW FORMAT DELIMITED
    FIELDS TERMINATED BY '\t';
```
2.输入数据
```
LOAD DATA LOCAL INPATH 'input/ncdc/micro-tab/sample.txt'
OVERWRITE INTO TABLE records;
```
查看hdfs中的表文件
```
dfs -ls /user/hive/warehouse/records/;
```
3.进行查询
```
SELECT year, MAX(temperature)
FROM records
WHERE temperature != 9999 AND quality IN (0, 1, 4, 5, 9)
GROUP BY year;
```

## 3. 运行Hive
### 1. 配置Hive(详见hive/set.hive)
命令行指定配置文件路径：
```
hive --config /Users/zzy/Docs/hadoop_book/ch17-hive/conf
```
命令行中设置属性：
```
hive -hiveconf fs.defaultFS=hdfs://localhost:9000 \
-hiveconf mapreduce.framework.name=yarn \
-hiveconf yarn.resourcemanager.address=localhost:9000
```
配置多个Hive用户共享一个Hadoop集群：
```
hadoop fs -mkdir /tmp
hadoop fs -chmod a+w /tmp
hadoop fs -mkdir -p /user/hive/warehouse
hadoop fs -chmod a+w /user/hive/warehouse
```
设置表的定义中都使用"桶"：
```
SET hive.enforce.bucketing=true;
```
查看：
```
SET hive.enforce.bucketing;
```
查看所有hive属性：
```
SET;
```
查看所有属性，包括hadoop：
```
SET -v;
```
1.执行引擎
设置Tez为hive的执行引擎(需安装Tez)：
```
SET hive.execution.engine=tez;
```
2.日志记录
指定日志记录位置：
```
hive -hiveconf hive.log.dir='/tmp/${user.name}'
```
设置将调试消息发送到控制台：
```
hive -hiveconf hive.root.logger=DEBUG,console
```
### 2. Hive服务
```
hive --service help
```

## 5. HiveQL
### 1. 数据类型
2. 复杂类型(详见hive/types.hive)
```
CREATE TABLE complex (
    c1 ARRAY<INT>,
    c2 MAP<STRING, INT>,
    c3 STRUCT<a:STRING, b:INT, c:DOUBLE>,
    c4 UNIONTYPE<STRING, INT>
);

SELECT c1[0], c2['b'], c3.c, c4 FROM complex;
```
### 2. 操作与函数
获取函数列表：
```
SHOW FUNCTIONS;
```
获得使用帮助：
```
DESCRIBE FUNCTION length;
```

#### 类型转换(hive/conversions.hive)

## 6. 表
### 1. 托管表 vs. 外部表
```
CREATE TABLE managed_table (dummy STRING);
LOAD DATA INPATH '/user/zzy/data.txt' INTO table managed_table;

DROP TABLE managed_table;
```

```
CREATE EXTERNAL TABLE external_table (dummy STRING)
    LOCATION '/user/zzy/external_table';
LOAD DATA INPATH '/user/zzy/data.txt' INTO TABLE external_table;

DROP TABLE external_table;
```

### 2. 分区、桶
#### 分区(详见hive/partitions.hive)
创建带分区的表：
```
CREATE TABLE logs (ts BIGINT, line STRING)
PARTITIONED BY (dt STRING, country STRING);
```
加载数据时，显式指定分区：
```
LOAD DATA LOCAL INPATH 'input/hive/partitions/file1'
INTO TABLE logs
PARTITION (dt='2001-01-01', country='GB');
```
查看分区：
```
SHOW PARTITIONS logs;
```
SELECT语句中使用分区列：
```
SELECT ts, dt, line
FROM logs
WHERE country='GB';
```
#### 桶(详见hive/buckets.hive)
指定划分桶所用的列和桶的个数：
```
CREATE TABLE bucketed_users (id INT, name STRING)
CLUSTERED BY (id) INTO 4 BUCKETS;
```
声明一个表，使用排序桶：
```
CREATE TABLE bucketed_users (id INT, name STRING)
CLUSTERED BY (id) SORTED BY (id ASC) INTO 4 BUCKETS;
```
解决“is running 328296960B beyond the 'VIRTUAL' memory limit. 
Current usage: 183.0 MB of 1 GB physical memory used; 
2.4 GB of 2.1 GB virtual memory used. Killing container.”问题
```
sudo vi /opt/hadoop/etc/hadoop/yarn-site.xml

<property>
  <name>yarn.scheduler.minimum-allocation-mb</name>
  <value>2048</value>
</property>

hive
SET mapred.child.java.opts=-Xmx2048m;
```

### 3. 存储格式
#### 默认分隔文本存储
#### 二进制存储格式(详见hive/storage.hive)
#### 定制的SerDe(序列化-反序列化)工具：RegexSerDe(详见hive/regex_serde.hive)
#### 存储句柄，集成HBase(https://cwiki.apache.org/confluence/display/Hive/HBaseIntegration)

### 4. 导入数据
#### INSERT语句
#### 多表插入(hive/multitable_insert.hive)
#### CTAS语句
```
CREATE TABLE target
AS
SELECT col1, col2
FROM source;
```

### 5. 表的修改
重命名表：
```
ALTER TABLE source RENAME TO target;
```
添加一个新列：
```
ALTER TABLE target ADD COLUMNS (col3 STRING);
```

### 6. 表的丢弃
删除表中所有数据，保留表的定义：
```
TRUNCATE TABLE my_table;
```
创建一个与前一个表模式相同的新表：
```
CREATE TABLE new_table LIKE existing_table;
```

### 7. 使用索引(hive/indexes.hive)

## 7. 查询
### 1. 排序和聚集
```
FROM records2
SELECT year, temperature
DISTRIBUTE BY year
SORT BY year ASC, temperature DESC;
```
### 2. MapReduce脚本(hive/mapreduce.hive)
报错：
```
Error: java.lang.RuntimeException: Hive Runtime Error while closing operators
        at org.apache.hadoop.hive.ql.exec.mr.ExecMapper.close(ExecMapper.java:207)
        at org.apache.hadoop.mapred.MapRunner.run(MapRunner.java:61)
        at org.apache.hadoop.mapred.MapTask.runOldMapper(MapTask.java:465)
        at org.apache.hadoop.mapred.MapTask.run(MapTask.java:349)
        at org.apache.hadoop.mapred.YarnChild$2.run(YarnChild.java:174)
        at java.security.AccessController.doPrivileged(Native Method)
        at javax.security.auth.Subject.doAs(Subject.java:422)
        at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1682)
        at org.apache.hadoop.mapred.YarnChild.main(YarnChild.java:168)
Caused by: org.apache.hadoop.hive.ql.metadata.HiveException: [Error 20003]: An error occurred when trying to close the Operator running your custom script.
        at org.apache.hadoop.hive.ql.exec.ScriptOperator.close(ScriptOperator.java:568)
        at org.apache.hadoop.hive.ql.exec.Operator.close(Operator.java:711)
        at org.apache.hadoop.hive.ql.exec.Operator.close(Operator.java:711)
        at org.apache.hadoop.hive.ql.exec.Operator.close(Operator.java:711)
        at org.apache.hadoop.hive.ql.exec.mr.ExecMapper.close(ExecMapper.java:189)
        ... 8 more


FAILED: Execution Error, return code 20003 from org.apache.hadoop.hive.ql.exec.mr.MapRedTask. An error occurred when trying to close the Operator running your custom script.
```
完整错误日志在
> http://192.168.32.129:8042/node/containerlogs/container_1523499936138_0019_01_000002/zzy/stderr/?start=-4096

中查看

发现错误：
> /usr/bin/env: 'python\r': No such file or directory

这是由于linux和windows系统下的默认换行符不一致，需要在vim中设置脚本格式：
https://github.com/GcsSloop/MacDeveloper/blob/master/Error/Python%E8%84%9A%E6%9C%AC:%20env:%20python%5Cr:%20No%20such%20file%20or%20directory.md
https://blog.csdn.net/sunshine_okey/article/details/7346283
```
vim is_good_quality.py
vim max_temperature_reduce.py

:set ff=unix
```

### 3. 连接(hive/joins.hive)
#### 内连接有两种实现方式：JOIN ON和FROM WHERE
##### EXPLAIN关键字查询执行计划的详细信息，EXPLAIN EXTENDED更详细
#### 外连接：左外、右外、全外
#### 半连接：注意右表只能在ON子句中出现，即不能在SELECT表达式中引用右表
#### map连接
启用优化选项
```
SET hive.optimize.bucketmapjoin=true;
```

### 4. 子查询(hive/max_temp_avg.hive)
注意必须为子查询赋予一个别名，以便外层查询访问结果

### 5. 视图(hive/views.hive)
```
CREATE VIEW
```
查看视图详细信息：
```
DESCRIBE EXTENDED valid_records;
```

## 8. 用户定义函数(hive/udfs.hive)
### 1. 写UDF(Strip.java)
在集群上使用本地JAR报错：
```
FAILED: Execution Error, return code 1
from org.apache.hadoop.hive.ql.exec.FunctionTask.
Hive warehouse is non-local,
but hive-examples.jar specifies file on local filesystem.
Resources on non-local warehouse should specify a non-local scheme/path
```
解决方法上传jar到集群

指明Hive类的路径：
方法一：
```
hive --auxpath /home/zzy/hive-examples.jar
```
方法二(多个路径用逗号分隔)：
```
vi hive-env.sh

HIVE_AUX_JARS_PATH=/home/zzy/path1,/home/zzy/path2
```

### 2. 写UDAF(Maximum.java, Mean.java)
