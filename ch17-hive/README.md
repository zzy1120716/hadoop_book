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

## 2. 示例
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
### 1. 配置Hive
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
2. 复杂类型
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