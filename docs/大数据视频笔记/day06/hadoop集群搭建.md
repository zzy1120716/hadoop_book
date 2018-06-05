#### 新建用户
```
useradd hadoop
passwd hadoop
hadoop
hadoop
```

#### 配置sudo（先在mini1上配，再分配给其他主机）
```
su
vi /etc/sudoers
root    ALL=(ALL)       ALL
hadoop  ALL=(ALL)       ALL
:wq!

scp /etc/sudoers mini2:/etc/
scp /etc/sudoers mini3:/etc/
scp /etc/sudoers mini4:/etc/
```
检验是否成功：
```
sudo hostname
```

#### 关闭防火墙：
```
service iptables stop
chkconfig iptables off
```

#### 安装hadoop：
```
mkdir apps
tar -zxvf hadoop-2.8.4.tar.gz -C apps/
cd apps/hadoop-2.8.4
cd /etc/hadoop
```
1. hadoop-env.sh
```
vi hadoop-env.sh
export JAVA_HOME=/usr/local/jdk1.8.0_172
```
2. core-site.xml
```
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://mini1:9000</value>
  </property>
  <property>
    <name>hadoop.tmp.dir</name>
    <value>/home/hadoop/hdpdata</value>
  </property>
```
3. hdfs-site.xml
```
  <property>
    <name>dfs.replication</name>
    <value>2</value>
  </property>
  <property>
    <name>dfs.secondary.http.address</name>
    <value>192.168.32.81:50090</value>
  </property>
```
4. mapred-site.xml
```
cp mapred-site.xml.template mapred-site.xml
vi mapred-site.xml
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
  </property>
```
5. yarn-site.xml
```
  <property>
    <name>yarn.resourcemanager.hostname</name>
    <value>mini1</value>
  </property>
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
  </property>
```

#### 分发
```
scp -r /home/hadoop/apps mini2:/home/hadoop/
scp -r /home/hadoop/apps mini3:/home/hadoop/
scp -r /home/hadoop/apps mini4:/home/hadoop/
```

#### 配置环境变量
```
sudo vi /etc/profile
export HADOOP_HOME=/home/hadoop/apps/hadoop-2.8.4
export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
```
```
分发：
sudo scp /etc/profile mini2:/etc/
sudo scp /etc/profile mini3:/etc/
sudo scp /etc/profile mini4:/etc/
```

#### HDFS格式化
```
hadoop namenode -format
```

#### 启动namenode
```
hadoop-daemon.sh start namenode
```

#### 启动datanode（mini2, mini3, mini4）
```
hadoop-daemon.sh start datanode
```

#### 停止节点
```
hadoop-daemon.sh stop datanode
hadoop-daemon.sh stop namenode
```

#### 查看日志
```
less /home/hadoop/apps/hadoop-2.8.4/logs/hadoop-hadoop-datanode-mini4.log
```

#### 配置自动化启动脚本，需要启动的从节点
```
vi /home/hadoop/apps/hadoop-2.8.4/etc/hadoop/slaves
mini2
mini3
mini4
```

#### 配置免密登录（从主节点到自己以及各个从节点）
```
ssh-keygen
ssh-copy-id mini1
ssh-copy-id mini2
ssh-copy-id mini3
ssh-copy-id mini4
```

#### 现在可以使用命令启动/停止所有节点
```
start-dfs.sh && start-yarn.sh
```