#### 测试MaxTemperatureWithCounters
1. mvn clean && mvn install
2. hadoop fs -rm -r input && hadoop fs -put input && hadoop fs -rm -r output-counters
3. hadoop jar target/ch09-mr-features-4.0.jar MaxTemperatureWithCounters \
input/ncdc/all output-counters
4. hadoop fs -cat output-counters/part-r-00000

#### 测试MissingTemperatureFields
hadoop jar target/ch09-mr-features-4.0.jar MissingTemperatureFields job_xxx_xxxx

#### SortDataPreprocessor将天气数据转成SequenceFile格式
1. mvn clean && mvn install
2. hadoop fs -rm -r input && hadoop fs -put input && hadoop fs -rm -r input/ncdc/all-seq
3. hadoop jar target/ch09-mr-features-4.0.jar SortDataPreprocessor input/ncdc/all \
input/ncdc/all-seq
##### 服务器上运行SortDataPreprocessor
修改/opt/hadoop-2.8.2/etc/hadoop/hadoop-env.sh "export HADOOP_ROOT_LOGGER=INFO,console"
1. hadoop fs -mkdir -p /user/root
2. hadoop fs -put input /user/root
3. hadoop jar ch09-mr-features-4.0.jar SortDataPreprocessor input/ncdc/all \
input/ncdc/all-seq
##### 注意：Mac OS X下运行会报错"缺少本地库"
参考：http://rockyfeng.me/hadoop_native_library_mac.html
https://gauravkohli.com/2014/09/28/building-native-hadoop-v-2-4-1-libraries-for-os-x/
##### 本身hadoop对mac支持就不好，测试排序程序推荐到linux系统

#### SortByTemperatureHashPartitioner
hadoop fs -rm -r /user/root/output-hashsort
hadoop jar ch09-mr-features-4.0.jar SortByTemperatureHashPartitioner \
-D mapreduce.job.reduces=30 input/ncdc/all-seq output-hashsort

#### SortByTemperatureUsingTotalOrderPartitioner
hadoop fs -rm -r /user/root/output-totalsort
hadoop jar ch09-mr-features-4.0.jar SortByTemperatureUsingTotalOrderPartitioner \
-D mapreduce.job.reduces=30 input/ncdc/all-seq output-totalsort

#### MaxTemperatureUsingSecondarySort
hadoop jar target/ch09-mr-features-4.0.jar MaxTemperatureUsingSecondarySort input/ncdc/all \
output-secondarysort

hadoop fs -cat output-secondarysort/part-r-00000 | sort | head

#### Streaming的辅助排序
1.
cd /Users/zzy/Docs/hadoop_book/ch09-mr-features/src/main/python

hadoop fs -rm -r output-secondarysort-streaming

hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
-D stream.num.map.output.key.fields=2 \
-D mapreduce.partition.keypartitioner.options=-k1,1 \
-D mapreduce.job.output.key.comparator.class=\
org.apache.hadoop.mapred.lib.KeyFieldBasedComparator \
-D mapreduce.partition.keycomparator.options="-k1n -k2nr" \
-files secondary_sort_map.py,secondary_sort_reduce.py \
-input input/ncdc/all \
-output output-secondarysort-streaming \
-mapper secondary_sort_map.py \
-partitioner org.apache.hadoop.mapred.lib.KeyFieldBasedPartitioner \
-reducer secondary_sort_reduce.py
##### 注意：若出现错误java.lang.RuntimeException: PipeMapRed.waitOutputThreads，说明python代码有bug
可以在命令行中python abc.py来查看代码语法错误

hadoop fs -cat output-secondarysort-streaming/part-00000 | sort | head

2.
hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
  -D stream.num.map.output.key.fields=2 \
  -files max_daily_temp_map.py,max_daily_temp_reduce.py \
  -input input/ncdc/all \
  -output out_max_daily \
  -mapper max_daily_temp_map.py \
  -reducer max_daily_temp_reduce.py

hadoop fs -cat out_max_daily/part-00000

3.
hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
  -D stream.num.map.output.key.fields=2 \
  -files mean_max_daily_temp_map.py,mean_max_daily_temp_reduce.py \
  -input out_max_daily \
  -output out_mean_max_daily \
  -mapper mean_max_daily_temp_map.py \
  -reducer mean_max_daily_temp_reduce.py
  
hadoop fs -cat out_mean_max_daily/part-00000

#### JoinRecordWithStationName
mvn clean && mvn install -DskipTests

hadoop fs -rm -r output-join

hadoop jar target/ch09-mr-features-4.0.jar JoinRecordWithStationName \
input/ncdc/all input/ncdc/metadata output-join
##### reduce阶段报错java.lang.NullPointerException(已解决：TextPair.java有误)
hadoop fs -cat output-join/part-r-00000

#### MaxTemperatureByStationNameUsingDistributedCacheFile
mvn clean && mvn install -DskipTests

hadoop fs -rm -r output-cache

hadoop jar target/ch09-mr-features-4.0.jar MaxTemperatureByStationNameUsingDistributedCacheFile \
-files input/ncdc/metadata/stations-fixed-width.txt input/ncdc/all output-cache

hadoop fs -cat output-cache/part-r-00000

#### TemperatureDistribution
hadoop fs -rm -r output-distribution

hadoop jar target/ch09-mr-features-4.0.jar TemperatureDistribution \
input/ncdc/all output-distribution

hadoop fs -cat output-distribution/part-r-00000
