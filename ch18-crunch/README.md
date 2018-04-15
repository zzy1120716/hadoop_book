## 1. 示例
##### MaxTemperatureCrunch
```
hadoop jar crunch-examples.jar com.zzy.hadoopbook.crunch.MaxTemperatureCrunch \
input/ncdc/sample.txt output
```
###### 注意首先要启动JobHistoryServer
```
mr-jobhistory-daemon.sh start historyserver
mr-jobhistory-daemon.sh stop historyserver
```
>NcdcRecordParser抛出java.io.NotSerializableException

解决：让NcdcRecordParser类implements Serializable

输出结果：
```
hadoop fs -cat output/part-r-00000
```

## 2. 核心API
### 1. 基本操作