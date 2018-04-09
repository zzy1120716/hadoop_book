#### Pig的安装与配置
```
tar zxf pig-0.17.0.tar.gz

ln -s pig-0.17.0 pig

vi ~/.zshrc
export PIG_HOME=/Users/zzy/pig
export PATH=$PATH:$PIG_HOME/bin

source ~/.zshrc

pig -help
```
#### 本地模式：启动Grunt
```
pig -x local
```
#### MapReduce模式
```
pig -brief
```
#### max_temp.pig
##### 注意首先要启动JobHistoryServer
```
mr-jobhistory-daemon.sh --config $HADOOP_HOME/etc/hadoop start historyserver
```

```
records = LOAD 'input/ncdc/micro-tab/sample.txt'
AS (year:chararray, temperature:int, quality:int);

DUMP records;

DESCRIBE records;
```

```
filtered_records = FILTER records BY temperature != 9999 AND
quality IN (0, 1, 4, 5, 9);

DUMP filtered_records;
```

```
grouped_records = GROUP filtered_records BY year;

DUMP grouped_records;

DESCRIBE grouped_records;
```

```
max_temp = FOREACH grouped_records GENERATE group,
MAX(filtered_records.temperature);

DUMP max_temp;
```
使用生成的输入子集显示逻辑计划的试运行结果：
```
ILLUSTRATE max_temp;
```
查看Pig创建的逻辑和物理计划：
```
EXPLAIN max_temp;
```

#### 通过HCatalog使用Hive
1.配置HCAT_HOME
```
vi ~/.zshrc

export HCAT_HOME=/Users/zzy/hive/hcatalog

export PATH=$PATH:$HCAT_HOME/bin

source ~/.zshrc
```
2.hive建表
```
hive

CREATE TABLE records
(
    year VARCHAR(100),
    temperature INT,
    quality INT
) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
STORED AS TEXTFILE;

LOAD DATA INPATH "input/ncdc/micro-tab/sample.txt" INTO TABLE records;
```
3.使用
```
pig -useHCatalog

records = LOAD 'records' USING org.apache.hive.hcatalog.pig.HCatLoader();

DESCRIBE records;

DUMP records;
```

#### 验证与空值
读取：
```
records = LOAD 'input/ncdc/micro-tab/sample_corrupt.txt'
AS (year:chararray, temperature:int, quality:int);

DUMP records;
```
过滤出空值记录：
```
corrupt_records = FILTER records BY temperature is null;

DUMP corrupt_records;
```
获取损坏记录条数：
```
grouped = GROUP corrupt_records ALL;
all_grouped = FOREACH grouped GENERATE group, COUNT(corrupt_records);
DUMP all_grouped;
```
划分数据：
```
SPLIT records INTO good_records IF temperature is not null,
bad_records OTHERWISE;

DUMP good_records;

DUMP bad_records;
```
若事先未声明temperature数据类型，遇到损坏数据，MAX函数会当做空值处理：
```
records = LOAD 'input/ncdc/micro-tab/sample_corrupt.txt'
AS (year:chararray, temperature, quality:int);
DUMP records;

filtered_records = FILTER records BY temperature != 9999 AND
quality IN (0, 1, 4, 5, 9);
grouped_records = GROUP filtered_records BY year;
max_temp = FOREACH grouped_records GENERATE group,
MAX(filtered_records.temperature);
DUMP max_temp;
```
使用SIZE函数对损坏数据进行过滤：
```
fs -put input/pig input

A = LOAD 'input/pig/corrupt/missing_fields';
DUMP A;

B = FILTER A BY SIZE(TOTUPLE(*)) > 1;
DUMP B;
```
#### 宏
```
IMPORT './src/main/pig/max_temp.macro';

records = LOAD 'input/ncdc/micro-tab/sample.txt'
AS (year:chararray, temperature:int, quality:int);
filtered_records = FILTER records BY temperature != 9999 AND
quality IN (0, 1, 4, 5, 9);
max_temp = max_by_group(filtered_records, year, temperature);
DUMP max_temp;
```

#### UDF
启动集群：
```
start-dfs.sh && start-yarn.sh
mr-jobhistory-daemon.sh --config $HADOOP_HOME/etc/hadoop start historyserver
```
指定本地jar路径：
```
REGISTER pig-examples.jar
```
调用：
```
records = LOAD 'input/ncdc/micro-tab/sample.txt'
AS (year:chararray, temperature:int, quality:int);

filtered_records = FILTER records BY temperature != 9999 AND 
com.zzy.hadoopbook.pig.IsGoodQuality(quality);

DUMP filtered_records;
```

本地包加入Grunt的搜索路径：
```
pig -Dudf.import.list=com.zzy.hadoopbook.pig

REGISTER pig-examples.jar

filtered_records = FILTER records BY temperature != 9999 AND 
IsGoodQuality(quality);
```
定义函数别名：
```
DEFINE isGood com.zzy.hadoopbook.pig.IsGoodQuality();

filtered_records = FILTER records BY temperature != 9999 AND isGood(quality);
```

#### Trim
```
mvn clean && mvn package -DskipTests
pig
```

```
REGISTER pig-examples.jar

A = LOAD 'input/pig/udfs/A'
AS (fruit:chararray);

DUMP A;
DESCRIBE A;

B = FOREACH A GENERATE com.zzy.hadoopbook.pig.Trim(fruit);
DUMP B;
DESCRIBE B;
```

#### 动态调用
```
DEFINE trim
InvokeForString('org.apache.commons.lang.StringUtils.trim','String');
B = FOREACH A GENERATE trim(fruit);
DUMP B;
```

#### CutLoadFunc，加载UDF

#### 使用PigStorage进行数据的加载和存储
```
A = LOAD 'input/pig/foreach/A';

STORE A INTO 'out' USING PigStorage(':');

cat out
```

#### FOREACH...GENERATE
```
DUMP A;

B = FOREACH A GENERATE $0, $2+1, 'Constant';

DUMP B;
```

#### year_stats.pig
```
pig src/main/pig/year_stats.pig
```

#### STREAM操作
```
A = LOAD 'input/pig/foreach/A'
    AS (f0:chararray, f1:chararray, f2:int);

C = STREAM A THROUGH `cut -f 2`;
DUMP C;
```

```
pig src/main/pig/max_temp_filter_stream.pig
```

#### JOIN、COGROUP、CROSS语句
join.pig

#### max_temp_station_name.pig
```
pig src/main/pig/max_temp_station_name.pig

hadoop fs -cat max_temp_by_station/part-r-00000
```

#### GROUP语句
group.grunt

#### 排序
sort.grunt

#### 组合与切分
combine.grunt

#### SET语句
set.grunt

#### 匿名关系
```
=> LOAD 'input/ncdc/micro-tab/sample.txt';
DUMP @
```

#### 参数代换
命令行方式
```
pig -param input=input/ncdc/micro-tab/sample.txt \
-param output=/tmp/out \
src/main/pig/max_temp_param.pig

hadoop fs -cat /tmp/out/part-r-00000
```
文件方式
```
pig -param_file src/main/pig/max_temp_param.param \
src/main/pig/max_temp_param.pig

hadoop fs -cat /tmp/out/part-r-00000
```
动态参数
```
pig -param input=input/ncdc/micro-tab/sample.txt \
-param output=/tmp/`date "+%Y-%m-%d" `/out \
src/main/pig/max_temp_param.pig

hadoop fs -cat /tmp/2018-04-07/out/part-r-00000
```
参数代换处理（代换后的脚本为max_temp_param.pig.substituted）
```
pig -dryrun -param input=input/ncdc/micro-tab/sample.txt \
-param output=/tmp/`date "+%Y-%m-%d" `/out \
src/main/pig/max_temp_param.pig
```