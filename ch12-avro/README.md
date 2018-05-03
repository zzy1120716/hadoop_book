#### 使用Avro命令行工具
```
java -jar avro-tools-1.8.2.jar
```

#### 通过avsc文件生成class
pom文件中添加avro maven插件
```
mvn compile
```

#### Python API
##### Python添加Avro支持
python2
```
sudo easy_install avro
```
python3
```
sudo pip3 install avro-python3
```
写入文件时，指定文件名为pairs.avro
```
python3 ch12-avro/src/main/py/write_pairs.py ch12-avro/output/pairs.avro
```
命令行中输入成对记录，Ctrl-D结束
```
a,1
c,2
b,3
b,2
```

测试avro-python3
```
python3 src/main/py/test_avro.py
```
##### 使用Avro工具集显示pair.avro的内容
1. 官网下载Avro tools的jar放到/Users/zzy/avro目录下（版本1.8.2）
2. 在Mac环境变量中设置AVRO_HOME
```
sudo vi ~/.zshrc
```
添加一行：
```
export AVRO_HOME=/Users/zzy/avro
```
使之生效：
```
source ~/.zshrc
```
3. 显示内容
```
java -jar $AVRO_HOME/avro-tools-1.8.2.jar tojson ch12-avro/output/pairs.avro
```

#### AvroGenericMaxTemperature
1. 打包
```
cd ch12-avro
mvn clean package -DskipTests
```
2. 声明环境变量
```
export HADOOP_CLASSPATH=avro-examples.jar
```
override version of Avro in Hadoop

```
export HADOOP_USER_CLASSPATH_FIRST=true
```
3. 运行
```
hadoop jar avro-examples.jar AvroGenericMaxTemperature \
input/ncdc/sample.txt output
```
4. 查看结果
```
java -jar $AVRO_HOME/avro-tools-1.8.2.jar tojson hdfs://localhost:9000/user/zzy/output/part-r-00000.avro

{"year":1949,"temperature":111,"stationId":"012650-99999"}
{"year":1950,"temperature":22,"stationId":"011990-99999"}
```

#### AvroSort
1. 检查输入数据
```
java -jar $AVRO_HOME/avro-tools-1.8.2.jar tojson input/avro/pairs.avro
```

2. 打包
```
mvn clean package -DskipTests
```

3. 运行排序
```
hadoop fs -rm -r output
hadoop fs -mkdir input/avro
hadoop fs -put input/avro/pairs.avro input/avro
hadoop jar avro-examples.jar AvroSort \
input/avro/pairs.avro output src/main/resources/SortedStringPair.avsc
```

4. 查看结果
```
java -jar $AVRO_HOME/avro-tools-1.8.2.jar tojson hdfs://localhost:9000/user/zzy/output/part-r-00000.avro

{"left":"b","right":"3"}
{"left":"b","right":"2"}
{"left":"c","right":"2"}
{"left":"a","right":"1"}
```

#### AvroProjection
```
hadoop jar avro-examples.jar AvroProjection \
input/ncdc/sample.txt output src/main/resources/StringPair.avsc
```

#### AvroSpecificMaxTemperature
```
hadoop jar avro-examples.jar AvroSpecificMaxTemperature \
input/ncdc/sample.txt output
```