### 1. HBase安装
#### 1. 解压，修改环境变量
```
tar -zxvf hbase-0.99.2-bin.tar.gz -C apps/
cd apps
mv hbase-0.99.2 hbase

sudo vi /etc/profile
export HBASE_HOME=/home/hadoop/apps/hbase
export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$HBASE_HOME/bin

cd /home/hadoop/apps/hbase/conf
```
#### 2. 修改配置文件
1. vi regionservers
```
mini2
mini3
mini4
```

2. vi hbase-env.sh
```
# The java implementation to use.  Java 1.7+ required.
export JAVA_HOME=/usr/local/jdk1.8.0_172/

# Extra Java CLASSPATH elements.  Optional.
# export HBASE_CLASSPATH=
export HBASE_CLASSPATH=/home/hadoop/apps/hadoop-2.8.4/etc/hadoop

# Tell HBase whether it should manage it's own instance of Zookeeper or not.
# export HBASE_MANAGES_ZK=true
export HBASE_MANAGES_ZK=false
```

3. vi hbase-site.xml
默认hbase的web界面是不开启的，需要在hdfs-site.xml文件中增加hbase.master.info.port属性
```
<configuration>
  <property>
  <name>hbase.master</name>
  <value>mini1:60000</value>
  </property>
  <property>
  <name>hbase.master.maxclockskew</name>
  <value>180000</value>
  </property>
  <property>
  <name>hbase.rootdir</name>
  <value>hdfs://mini1:9000/hbase</value>
  </property>
  <property>
  <name>hbase.cluster.distributed</name>
  <value>true</value>
  </property>
  <property>
  <name>hbase.zookeeper.quorum</name>
  <value>mini1,mini2,mini3</value>
  </property>
  <property>
  <name>hbase.zookeeper.property.dataDir</name>
  <value>/home/hadoop/apps/hbase/tmp/zookeeper</value>
  </property>
  <!-- 新增的配置 -->
  <property>
  <name>hbase.master.info.port</name>
  <value>60010</value>
  </property>
  <!-- 新增的配置 -->
</configuration>
```

4. 把hadoop的 hdfs-site.xml 和 core-site.xml 放到hbase/conf下
```
cp /home/hadoop/apps/hadoop-2.8.4/etc/hadoop/hdfs-site.xml
cp /home/hadoop/apps/hadoop-2.8.4/etc/hadoop/core-site.xml
```

#### 3. 分配到其他机器
```
scp -r /home/hadoop/apps/hbase hadoop@mini2:/home/hadoop/apps
scp -r /home/hadoop/apps/hbase hadoop@mini3:/home/hadoop/apps
scp -r /home/hadoop/apps/hbase hadoop@mini4:/home/hadoop/apps
```

#### 4. 启动
```
start-dfs.sh
start-yarn.sh
zkServer.sh start
start-hbase.sh
```

#### 5. 查看
    进程：jps
    进入hbase的shell：hbase shell
    退出hbase的shell：quit
    页面：http://master:60010/ 

### 2. 配置HMaster的主备(双主节点)
```
local-master-backup.sh start 2
```

### 3. 动态添加HBase节点
1. 复制原子节点到新节点
```
hbase-daemon.sh start regionserver
```