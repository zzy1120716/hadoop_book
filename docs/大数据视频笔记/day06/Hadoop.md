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
```