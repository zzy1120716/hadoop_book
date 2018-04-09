#!/usr/bin/env bash

# 1.生成随机数据
hadoop jar \
$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar \
teragen -Dmapreduce.job.maps=1000 10t random-data

hadoop jar \
$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar \
teragen -Dmapreduce.job.maps=1 10m random-data

# 2.运行TeraSort
hadoop jar \
$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar \
terasort random-data sorted-data

# 3.验证
hadoop jar \
$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar \
teravalidate sorted-data report

# 4.查看report
hadoop fs -cat report/part-r-00000