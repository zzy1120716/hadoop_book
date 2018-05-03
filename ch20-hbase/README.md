## 20.2 概念
#### HBase表 vs. RDBMS表
1.HBase单元格有版本
2.HBase表的行是排序的
3.HBase表中只要列族预先存在，客户端随时可以把列添加到列族中

#### HBase区域名 = 表名 + ',' + 起始行 + ',' + 创建时间 + '.' + 前三项整体的MD5值 + '.'

## 20.3 安装
#### 安装HBase
```
tar -zxf hbase-0.98.7-hadoop2-bin.tar.gz

ln -s hbase-0.98.7-hadoop2 hbase

vi ~/hbase/conf/hbase-env.sh
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home

vi ~/.zshrc
export HBASE_HOME=/Users/zzy/hbase
export PATH=$PATH:$HBASE_HOME/bin

source ~/.zshrc

hbase
```

#### 安装ZooKeeper
```
tar -zxf zookeeper-3.4.6.tar.gz

ln -s zookeeper-3.4.6 zookeeper

vi ~/.zshrc
export ZOOKEEPER_HOME=/Users/zzy/zookeeper
export PATH=$PATH:$ZOOKEEPER_HOME/bin

mkdir zookeeper_data

source ~/.zshrc

vi ~/zookeeper/conf/zoo.cfg
tickTime=2000
dataDir=/Users/zzy/zookeeper_data
clientPort=2181

zkServer.sh start

echo ruok | nc localhost 2181
```

#### HBase伪分布式配置
http://hbase.apache.org/book.html#configuration
```
vi ~/hbase/conf/hbase-site.xml

<configuration>
  <property>
    <name>hbase.rootdir</name>
    <value>hdfs://localhost:9000/hbase</value>
  </property>
  <property>
    <name>hbase.zookeeper.property.dataDir</name>
    <value>/Users/zzy/zookeeper_data</value>
  </property>
  <property>
    <name>hbase.cluster.distributed</name>
    <value>true</value>
  </property>
  <property>    
    <name>hbase.zookeeper.property.clientPort</name>    
    <value>2181</value>    
  </property>  
  <property>    
    <name>hbase.zookeeper.quorum</name>    
    <value>localhost</value>    
  </property>
</configuration>
```

```
vi ~/hbase/conf/regionservers

localhost
```

##### 自己安装zookeeper，启动及关闭先后顺序为：启动Hadoop—>启动ZooKeeper集群—>启动HBase—>停止HBase—>停止ZooKeeper集群—>停止Hadoop

#### 测试驱动
启动持久化存储的HBase临时实例：
```
start-hbase.sh
```
管理HBase实例：
```
hbase shell
hbase(main):001:0> help
```

#### 命令行示例
创建表：
```
hbase(main):001:0> create 'test', 'data'
```
输出表名：
```
hbase(main):002:0> list
```
插入数据，获取第一行，获取全部数据：
```
hbase(main):003:0> put 'test', 'row1', 'data:1', 'value1'
hbase(main):004:0> put 'test', 'row2', 'data:2', 'value2'
hbase(main):005:0> put 'test', 'row3', 'data:3', 'value3'
hbase(main):006:0> get 'test', 'row1'
hbase(main):007:0> scan 'test'
```
禁用表、删除表（删除前必须先禁用）：
```
hbase(main):008:0> disable 'test'
hbase(main):009:0> drop 'test'
hbase(main):010:0> list
```
退出：
```
hbase(main):011:0> exit
```

#### 关闭HBase实例：
```
stop-hbase.sh
```

## 20.4 客户端
### 20.4.1 Java
##### ExampleClient
##### NewExampleClient (HBase 1.0 version uses Connection, Admin, and Table)
```
start-dfs.sh && start-yarn.sh
zkServer.sh start
start-hbase.sh
mvn package
export HBASE_CLASSPATH=hbase-examples.jar
hbase ExampleClient
```

### 20.4.2 MapReduce
##### SimpleRowCounter
```
mvn clean package
export HBASE_CLASSPATH=hbase-examples.jar
hbase SimpleRowCounter test
```
作用与 `hbase(main):001:0> count 'test'`相同

##### 出现错误：Exception in thread "main" java.lang.IllegalArgumentException: No enum constant org.apache.hadoop.mapreduce.JobCounter.MB_MILLIS_MAPS

## 20.5 创建在线查询应用
### 20.5.1 模式设计
```
hbase(main):001:0> create 'stations', {NAME => 'info'}
hbase(main):002:0> create 'observations', {NAME => 'data'}
```

### 20.5.2 加载数据
##### HBaseStationImporter
##### NewHBaseStationImporter
```
hbase HBaseStationImporter input/ncdc/metadata/stations-fixed-width.txt
```

##### HBaseTemperatureImporter
```
hbase HBaseTemperatureImporter input/ncdc/all
```

##### 出现错误：java.lang.ClassNotFoundException: Class org.apache.hadoop.hbase.mapreduce.TableOutputFormat not found

##### HBaseTemperatureDirectImporter

##### HBaseTemperatureBulkImporter

### 20.5.3 在线查询
##### HBaseStationQuery
##### NewHBaseStationQuery
```
hbase HBaseStationQuery 011990-99999
```

##### HBaseTemperatureQuery
```
hbase HBaseTemperatureQuery 011990-99999
```

#### 停止集群
```
stop-hbase.sh && zkServer.sh stop && stop-all.sh
```

#### 启动集群
```
start-dfs.sh && start-yarn.sh && zkServer.sh start && start-hbase.sh
```

## 20.6 HBase和RDBMS的比较

## 20.7 Praxis
### 20.7.3 用户界面 —— localhost:60010