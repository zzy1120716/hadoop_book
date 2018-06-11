```
hadoop fs -mkdir -p /data/clickLog/20151226/
mkdir -p /home/hadoop/logs/log/
mkdir -p /home/hadoop/logs/toupload/
```
Export Runnable Jar...
https://blog.csdn.net/xuemengrui12/article/details/74984731

```
scp /Users/zzy/Docs/hadoop_book/out/artifacts/log_jar/log.jar hadoop@mini1:~/

vi datacollect.sh

chmod +x datacollect.sh

./datacollect.sh
```