#### 安装配置Flume
```
tar xzf apache-flume-1.8.0-bin.tar.gz
sudo vi ~/.zshrc
export FLUME_HOME=/Users/zzy/apache-flume-1.8.0-bin
export PATH=$PATH:$FLUME_HOME/bin
source ~/.zshrc
flume-ng
```

#### 示例1：source-channel-sink(logger)
1. 创建缓冲目录
```
mkdir -p ~/flume_tmp/spooldir
```

2. 启动Flume代理
```
flume-ng agent \
--conf-file spool-to-logger.properties \
--name agent1 \
--conf $FLUME_HOME/conf \
-Dflume.root.logger=INFO,console
```

3. 写文件测试
```
echo "Hello Flume" > /Users/zzy/flume_tmp/spooldir/.file1.txt
mv /Users/zzy/flume_tmp/spooldir/.file1.txt /Users/zzy/flume_tmp/spooldir/file1.txt
```
代理输出：
```
Preparing to move file /Users/zzy/flume_tmp/spooldir/file1.txt to /Users/zzy/flume_tmp/spooldir/file1.txt.COMPLETED
(SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 48 65 6C 6C 6F 20 46 6C 75 6D 65                Hello Flume }
```

#### 示例2：source-channel-sink(hdfs)
1. 启动Flume代理
```
flume-ng agent \
--conf-file spool-to-hdfs.properties \
--name agent1 \
--conf $FLUME_HOME/conf \
-Dflume.root.logger=INFO,console
```

2. 写文件测试
```
echo -e "Hello\nAgain" > /Users/zzy/flume_tmp/spooldir/.file2.txt
mv /Users/zzy/flume_tmp/spooldir/.file2.txt /Users/zzy/flume_tmp/spooldir/file2.txt
```

3. 查看内容
```
hadoop fs -cat /tmp/flume/events.1522315948068.log
```

#### 示例3：spool-to-hdfs-partitioned(增加时间戳拦截器，目录按时间布局，粒度为"天")
```
flume-ng agent \
--conf-file spool-to-hdfs-partitioned.properties \
--name agent1 \
--conf $FLUME_HOME/conf \
-Dflume.root.logger=INFO,console
```

#### 示例4：spool-to-hdfs-avro(配置将事件写到一个Snappy压缩的Avro文件中)
```
flume-ng agent \
--conf-file spool-to-hdfs-avro.properties \
--name agent1 \
--conf $FLUME_HOME/conf \
-Dflume.root.logger=INFO,console
```

#### 示例5：spool-to-hdfs-and-logger(同时传递事件到HDFS sink和logger sink)
```
flume-ng agent \
--conf-file spool-to-hdfs-and-logger.properties \
--name agent1 \
--conf $FLUME_HOME/conf \
-Dflume.root.logger=INFO,console
```

#### 示例6：spool-to-hdfs-tiered(分层)
```
flume-ng agent \
--conf-file spool-to-hdfs-tiered.properties \
--name agent1 \
--conf $FLUME_HOME/conf \
-Dflume.root.logger=INFO,console
```

```
flume-ng agent \
--conf-file spool-to-hdfs-tiered.properties \
--name agent2 \
--conf $FLUME_HOME/conf \
-Dflume.root.logger=INFO,console
```

#### 示例7：spool-to-hdfs-tiered-load-balance(负载均衡)
```
flume-ng agent \
--conf-file spool-to-hdfs-tiered-load-balance.properties \
--name agent1 \
--conf $FLUME_HOME/conf \
-Dflume.root.logger=INFO,console
```

```
flume-ng agent \
--conf-file spool-to-hdfs-tiered-load-balance.properties \
--name agent2a \
--conf $FLUME_HOME/conf \
-Dflume.root.logger=INFO,console
```

```
flume-ng agent \
--conf-file spool-to-hdfs-tiered-load-balance.properties \
--name agent2b \
--conf $FLUME_HOME/conf \
-Dflume.root.logger=INFO,console
```