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
##### PrimitiveOperationsTest
实现一个工具类PCollections，打印PCollection和PTable对象

### 2. 类型
##### TypesTest

### 3. 源和目标
##### TypesTest
##### SourcesAndTargetsTest

### 4. 函数
#### 1. 序列化
##### SerializableFunctionsTest
CustomDoFn
##### NonSerializableOuterClass

#### 2. 对象重用
##### ObjectReuseTest

### 5. 物化
##### MaterializeTest

## 3. 管线执行
### 1. 运行管线
#### 1. 异步执行
##### PipelineExecutionTest
#### 2. 调试
##### PipelineDebugTest

### 2. 停止管线
### 3. 查看Crunch计划
##### PipelineExecutionTest
###### 安装Graphviz将.dot文件转换为png图像

##### CheckpointTest
```
brew install graphviz
dot target/testInspectPlan.dot -T png -o testInspectPlan.png
dot target/testInspectSynchronous.dot -T png -o testInspectSynchronous.png
dot target/testComplexPipeline.dot -T png -o testComplexPipeline.png
dot target/testParallelDoFusion.dot -T png -o testParallelDoFusion.png
dot target/testSiblingFusion.dot -T png -o testSiblingFusion.png
```

### 4. 迭代算法

## 4. Crunch库
### 排序
##### SortTest
### 连接
##### JoinTest

##### 重点：PageRankTest中PageRank算法的实现
```
dot target/pagerank-0.dot -T png -o pagerank-0.png
dot target/pagerank-1.dot -T png -o pagerank-1.png
dot target/pagerank-2.dot -T png -o pagerank-2.png
dot target/pagerank-3.dot -T png -o pagerank-3.png
dot target/pagerank-4.dot -T png -o pagerank-4.png
dot target/pagerank-5.dot -T png -o pagerank-5.png
dot target/pagerank-6.dot -T png -o pagerank-6.png
dot target/pagerank-7.dot -T png -o pagerank-7.png
dot target/pagerank-8.dot -T png -o pagerank-8.png
dot target/pagerank-9.dot -T png -o pagerank-9.png
```

## 5. 案例
##### SortByTemperatureCrunch
```
mvn clean package -DskipTests

start-dfs.sh && start-dfs.sh && mr-jobhistory-daemon.sh start historyservers

hadoop jar crunch-examples.jar \
com.zzy.hadoopbook.crunch.SortByTemperatureCrunch \
input/ncdc/sample.txt \
output-crunch-sort

hadoop fs -cat output-crunch-sort/part-r-000
```

##### GBK操作 ==> groupByKey()

##### SplitCrunch
```
hadoop jar crunch-examples.jar  com.zzy.hadoopbook.crunch.SplitCrunch
```

##### JoinRecordWithStationNameCrunch
```
hadoop jar crunch-examples.jar \
com.zzy.hadoopbook.crunch.JoinRecordWithStationNameCrunch \
input/ncdc/sample.txt \
input/ncdc/metadata/stations-fixed-width.txt \
output

hadoop fs -cat output/part-r-00000
```

##### MaxTemperatureWithCompressionCrunch
```
hadoop jar crunch-examples.jar \
com.zzy.hadoopbook.crunch.MaxTemperatureWithCompressionCrunch \
input/ncdc/sample.txt.gz \
output

hadoop fs -get output  

gunzip -c output/part-r-00000.gz 
```

##### MaxTemperatureWithCountersCrunch
```
hadoop jar crunch-examples.jar \
com.zzy.hadoopbook.crunch.MaxTemperatureWithCountersCrunch \
input/ncdc/sample.txt \
output

hadoop fs -cat output/part-r-00000
```

##### MaxTemperatureWithMultipleInputsCrunch
```
hadoop jar crunch-examples.jar \
com.zzy.hadoopbook.crunch.MaxTemperatureWithMultipleInputsCrunch \
input/ncdc/sample.txt \
input/metoffice/valleydata.txt \
output

hadoop fs -cat output/part-r-00000
```

##### MaxTemperatureUsingSecondarySortCrunch
```
hadoop jar crunch-examples.jar \
com.zzy.hadoopbook.crunch.MaxTemperatureUsingSecondarySortCrunch \
input/ncdc/sample.txt \
output

hadoop fs -cat output/part-r-00000
```

##### MaxTemperatureCrunchWithShutdownHook
```
hadoop jar crunch-examples.jar \
com.zzy.hadoopbook.crunch.MaxTemperatureCrunchWithShutdownHook \
input/ncdc/sample.txt \
output

hadoop fs -cat output/part-r-00000
```

##### AvroGenericMaxTemperatureCrunch
```
hadoop jar crunch-examples.jar \
com.zzy.hadoopbook.crunch.AvroGenericMaxTemperatureCrunch \
input/ncdc/sample.txt \
output
```

##### MaxTemperatureByStationNameCrunch
```
hadoop jar crunch-examples.jar \
com.zzy.hadoopbook.crunch.MaxTemperatureByStationNameCrunch \
input/ncdc/sample.txt \
input/ncdc/metadata/stations-fixed-width.txt \
output

hadoop fs -cat output/part-m-00000
```