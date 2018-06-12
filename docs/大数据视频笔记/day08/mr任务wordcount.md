```
scp /Users/zzy/Docs/hadoop_book/out/artifacts/wordcount/wordcount.jar hadoop@mini1:~/

hadoop fs -mkdir -p /wordcount/input
hadoop fs -put NOTICE.txt LICENSE.txt README.txt /wordcount/input
java -cp wordcount.jar cn.itcast.bigdata.mr.wcdemo.WordcountDriver /wordcount/input /wordcount/output （报错 NoClassDefFoundError）

hadoop jar wordcount.jar cn.itcast.bigdata.mr.wcdemo.WordcountDriver /wordcount/input /wordcount/output

hadoop fs -cat /wordcount/output/part-r-00000
```
