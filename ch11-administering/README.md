### 1. HDFS
#### 查看namenode是否处于安全模式
hdfs dfsadmin -safemode get

#### 暂时退出安全模式
hdfs dfsadmin -safemode wait

#### 进入安全模式
hdfs dfsadmin -safemode enter

#### 离开安全模式
hdfs dfsadmin -safemode leave

#### 启动日志审计功能
在hadoop-env.sh中增加：
export HDFS_AUDIT_LOGGER="INFO,RFAAUDIT"
hadoop jar /Users/zzy/Docs/hadoop_book/ch03-hdfs/jars/ListStatus.jar ListStatus /user/zzy

#### 查找一个文件的数据块
hdfs fsck /user/zzy/output-secondarysort/part-r-00000 -files -blocks -racks

```
Connecting to namenode via http://localhost:50070/fsck?ugi=zzy&files=1&blocks=1&racks=1&path=%2Fuser%2Fzzy%2Foutput-secondarysort%2Fpart-r-00000
FSCK started by zzy (auth:SIMPLE) from /127.0.0.1 for path /user/zzy/output-secondarysort/part-r-00000 at Mon Mar 26 14:12:40 CST 2018
/user/zzy/output-secondarysort/part-r-00000 18 bytes, 1 block(s):  OK
0. BP-225540568-10.210.25.4-1516549443235:blk_1073742797_1973 len=18 Live_repl=1 [/default-rack/127.0.0.1:50010]
```

##### datanode块扫描器
http://datanode:50075/blockScannerReport
http://localhost:50075/blockScannerReport

#### datanode均衡器
start-balancer.sh

### 2. 监控
#### 设置日志级别
http://resource-manager-host:8088/logLevel
http://localhost:8088/logLevel

##### 临时修改
hadoop daemonlog -setlevel resource-manager-host:8088 \
org.apache.hadoop.yarn.server.resourcemanager DEBUG

hadoop daemonlog -setlevel localhost:8088 \
org.apache.hadoop.yarn.server.resourcemanager DEBUG

##### 永久修改
修改/添加log4j.properties
log4j.logger.org.apache.hadoop.yarn.server.resourcemanger=DEBUG

#### 获取堆栈跟踪
http://resource-manager-host:8088/stacks
http://localhost:8088/stacks

#### 度量和JMX
http://namenode-host:50070/jmx
http://localhost:50070/jmx

##### 允许远程监控：
在hadoop-env.sh中设置
HADOOP_NAMENODE_OPTS="-Dcom.sum.management.jmxremote.port=8004"

### 3. 维护
#### 元数据备份
hdfs dfsadmin -fetchImage fsimage.backup

##### 测试副本完整性
Offline Image Viewer
hdfs oiv

Offline Edits Viewer
hdfs oev

#### 委任和解除节点
修改hdfs-site.xml文件，指定include和exclude文件位置
```
<!-- include文件设置 -->
<property>
<name>dfs.hosts</name>
<value>/usr/local/hadoop/conf/datanode-allow-list</value>
</property>
 
<!-- exclude文件设置 -->
<property>
<name>dfs.hosts.exclude</name>
<value>/usr/local/hadoop/conf/datanode-deny-list</value>
</property>
```

#### 升级