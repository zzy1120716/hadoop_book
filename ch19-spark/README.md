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