## 19.1 安装Spark
```
mv Downloads/spark-2.3.0-bin-hadoop2.7.tgz .
tar -zxf spark-2.3.0-bin-hadoop2.7.tgz
ln -s spark-2.3.0-bin-hadoop2.7 spark

sudo vi ~/.zshrc
export SPARK_HOME=/User/zzy/spark
export PATH=$PATH:$SPARK_HOME/bin

source ~/.zshrc
spark-shell

scala> :q
scala> :quit
```

## 19.2 示例
```
scala> var lines = sc.textFile("input/ncdc/micro-tab/sample.txt")

scala> val records = lines.map(_.split("\t"))

scala> val filtered = records.filter(rec => (rec(1) != "9999" && rec(2).matches("[01459]")))

scala> val tuples = filtered.map(rec => (rec(0).toInt, rec(1).toInt))

scala> val maxTemps = tuples.reduceByKey((a, b) => Math.max(a, b))

scala> maxTemps.foreach(println(_))

scala> maxTemps.saveAsTextFile("output")

scala> :q

cat output/part-*
```

### 19.2.2 Scala独立应用
```
spark-submit --class MaxTemperature --master local \
spark-examples.jar input/ncdc/micro-tab/sample.txt output
```

### 19.2.3 Java示例
```
spark-submit --class MaxTemperatureSpark --master local \
spark-examples.jar input/ncdc/micro-tab/sample.txt output
```

### 19.2.4 Python示例
```
spark-submit --master local \
src/main/python/MaxTemperature.py \
input/ncdc/micro-tab/sample.txt output
```
或使用 `pyspark`

## 19.3 弹性分布式数据集(RDD)
### 19.3.1 创建
##### RDDCreationTest
###### 错误：java.lang.IncompatibleClassChangeError: Found interface org.apache.hadoop.mapreduce.TaskAttemptContext, but class was expected
###### 由于默认情况下使用hadoop1编译Avro，应将依赖改为hadoop2
###### 即在avro-mapred的dependency中增加一行
`<classifier>hadoop2</classifier>`

### 19.3.2 转换和动作
##### TransformationsAndActionsTest

### 19.3.3 持久化
##### SimpleTest

### 19.3.4 序列化
##### CustomKryoRegistrator
##### ReflectWeatherRecord
##### DataSerializationTest
##### FunctionSerializationTest

## 19.4 共享变量
### 19.4.1 广播变量
### 19.4.2 累加器
##### SharedDataTest

## 19.5 剖析Spark作业运行机制
### 19.5.2 DAG的构建
##### WordCountHistogramTest

## 19.6 执行器和集群管理器
##### MaxTemperatureWithPlacement，SparkContext第二个参数传递一个优选位置
```
spark-submit --class MaxTemperatureWithPlacement --master local \
spark-examples.jar input/ncdc/micro-tab/sample.txt output
```

##### 运行SimpleTest.test()时出现错误:
##### java.lang.NoSuchMethodError: scala.Predef$.refArrayOps([Ljava/lang/Object;)Lscala/collection/mutable/ArrayOps;
##### 原因：scalatest版本与scala compiler版本不一致
Project Structure -> Global Libraries

##### java.lang.NoSuchMethodError: io.netty.buffer.PooledByteBufAllocator.metric()Lio/netty/buffer/PooledByteBufAllocatorMetric;
把spark版本从2.3.0改为1.2.2，spark-1.x.x和spark-2.x.x很多API都不一样
