#### 测试MinimalMapReduce
1. export HADOOP_CLASSPATH=./target/ch08-mr-types-4.0.jar 
2. hadoop fs -put input
3. hadoop MinimalMapReduce "input/ncdc/all/190{1,2}.gz" output
4. hadoop fs -cat output/part-r-00000

#### 测试MinimalMapReduceWithDefaults
1. common项目中创建JobBuilder类
2. hadoop fs -rm -r output
3. export HADOOP_CLASSPATH=./target/ch08-mr-types-4.0.jar:../common/target/common-4.0.jar
4. hadoop MinimalMapReduceWithDefaults "input/ncdc/all/190{1,2}.gz" output
5. hadoop fs -cat output/part-r-00000

#### 最小化的Streaming作业
hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
  -input input/ncdc/sample.txt \
  -output output \
  -mapper /bin/cat

#### 默认的Streaming作业
1. hadoop fs -rm -r output
2. 命令行（除-input -output -mapper外，都是默认设置）
% hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
-input input/ncdc/sample.txt \
-output output \
-inputformat org.apache.hadoop.mapred.TextInputFormat \
-mapper /bin/cat \
-partitioner org.apache.hadoop.mapred.lib.HashPartitioner \
-numReduceTasks 1 \
-reducer org.apache.hadoop.mapred.lib.IdentityReducer \
-outputformat org.apache.hadoop.mapred.TextOutputFormat \
-io text
3. 注意Streaming使用旧MapReduce API
4. hadoop fs -cat output/part-00000

#### 测试WholeFileInputFormat
1. WholeFileRecordReader用RecordReader将整个文件读为一条记录

#### 测试SmallFilesToSequenceFileConverter
1. mvn clean && mvn install
2. hadoop fs -rm -r input && hadoop fs -put input && hadoop fs -rm -r output
3. export HADOOP_CLASSPATH=../common/target/common-4.0.jar
4. hadoop jar target/ch08-mr-types-4.0.jar SmallFilesToSequenceFileConverter \
-conf conf/hadoop-localhost.xml -D mapreduce.job.reduces=2 \
input/smallfiles output

##### 检查输出的顺序文件
1. hadoop fs -conf conf/hadoop-localhost.xml -text output/part-r-00000
2. hadoop fs -conf conf/hadoop-localhost.xml -text output/part-r-00001

#### 定制StationPartitioner
1. common中创建NcdcRecordParser，增加对StationId的解析，其余同第6章
2. 测试类NcdcRecordParserTest
3. common中创建MetOfficeRecordParser，增加对StationId的解析，其余同第6章
4. 测试类MetOfficeRecordParserTest

#### 测试MaxTemperatureWithMultipleInputs
在项目中增加类MaxTemperature等
1. mvn clean && mvn install
2. hadoop fs -rm -r input && hadoop fs -put input && hadoop fs -rm -r output
3. export HADOOP_CLASSPATH=../common/target/common-4.0.jar:../ch06-mr-dev/hadoop-examples.jar
4. hadoop jar target/ch08-mr-types-4.0.jar MaxTemperatureWithMultipleInputs \
-conf conf/hadoop-localhost.xml \
input/ncdc/micro/sample.txt input/metoffice output
5. hadoop fs -ls output
6. hadoop fs -cat output/part-r-00000

#### 测试PartitionByStationUsingMultipleOutputs
1. hadoop fs -rm -r output
2. export HADOOP_CLASSPATH=./target/ch08-mr-types-4.0.jar:../common/target/common-4.0.jar
3. hadoop jar target/ch08-mr-types-4.0.jar PartitionByStationUsingMultipleOutputs "input/ncdc/all/190{1,2}.gz" output
4. hadoop fs -ls output
5. hadoop fs -cat output/029070-99999-r-00000

#### 测试PartitionByStationYearUsingMultipleOutputs
1. hadoop fs -rm -r output
2. export HADOOP_CLASSPATH=./target/ch08-mr-types-4.0.jar:../common/target/common-4.0.jar
3. hadoop jar target/ch08-mr-types-4.0.jar PartitionByStationYearUsingMultipleOutputs "input/ncdc/all/190{1,2}.gz" output
4. hadoop fs -ls output/029070-99999/1901
5. hadoop fs -cat output/029070-99999/1901/part-r-00000
