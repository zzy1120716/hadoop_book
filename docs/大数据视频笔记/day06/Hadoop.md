### Hadoop生态环境
Azkaban所工作流调度，Oozie太重了

Google：GFS、BigTable、MapReduce
Nutch：HDFS、HBASE、MapReduce

网站点击流日志分析系统

浏览器中查看网络请求，可以看到类似"log.gif?sc=xxx"的请求，就是用来返回给服务器，做行为分析的 

获取方式：在网页中埋一段js代码

#### hadoop shell
```
hadoop fs -ls /
hadoop fs -put hello.txt /
hadoop fs -ls /
hadoop fs -cat /hello.txt
<<<<<<< HEAD

hadoop fs -put zookeeper.out /wordcount/input
cd apps/hadoop-2.8.4/share/hadoop/mapreduce
hadoop jar hadoop-mapreduce-examples-2.5.1.jar wordcount /wordcount/input/ /wordcount/output

hadoop fs -cat /wordcount/output/part-r-00000

hadoop fs -getmerge /wordcount/input /wordcount/output merg.file
hadoop fs -getmerge /wordcount/input/*.* merg.file 

hadoop fs -df -h /
hadoop fs -du -s -h hdfs://mini1:9000/*
hadoop fs -count /
hadoop fs -setrep 3 /hello.txt -- 设定副本数量
```
##### 这里设置的副本数知识记录在namenode的元数据中，是否真的会有这么多副本，还是要看datanode的数量

#### hdfs client java api
要在windows下测试，需要在windows平台下重新编译hadoop
##### 权限问题：windows与hadoop集群用户名不同
JavaVisualVM
JavaVMConsole监控JVM参数
```
Edit Configurations...
VM Options
-DHADOOP_USER_NAME=hadoop
```

=======
```
>>>>>>> origin/master
