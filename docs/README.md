# 1 搭建集群
## 1.1 安装jdk
https://my.oschina.net/dyyweb/blog/634067
```
sudo tar -zxf jdk-8u161-linux-x64.tar

sudo ln -s jdk1.8.0_161 latest

vi ~/.bash_profile
export JAVA_HOME=/usr/java/latest 
export PATH=$JAVA_HOME/bin:$PATH 
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar 

source ~/.bash_profile

java -version
```
## 1.2 安装Hadoop
http://hadoop.apache.org/docs/r3.1.0/hadoop-project-dist/hadoop-common/SingleCluster.html
```
sudo tar -zxf hadoop-3.1.0.tar.gz

sudo ln -s hadoop-3.1.0 hadoop

vi hadoop-env.sh
# set to the root of Java installation
export JAVA_HOME=/usr/java/latest

vi ~/.bash_profile
export HADOOP_HOME=/opt/hadoop
export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$JAVA_HOME/bin:$PATH
```
配置伪分布模式：
```
vi /opt/hadoop/etc/hadoop/core-site.xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>

vi /opt/hadoop/etc/hadoop/hdfs-site.xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
</configuration>
```
ssh免密登录：
```
sudo apt-get install openssh-server

ssh localhost

ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys
```
单节点Yarn配置：
```
vi /opt/hadoop/etc/hadoop/mapred-site.xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
        <name>mapreduce.application.classpath</name>
        <value>$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/*:$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/lib/*</value>
    </property>
</configuration>

vi /opt/hadoop/etc/hadoop/yarn-site.xml
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.env-whitelist</name>
        <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME</value>
    </property>
</configuration>
```
格式化namenode
```
hdfs namenode -format
```
启动hdfs和yarn：
```
start-dfs.sh && start-yarn.sh
```
namenode webapp地址： 
>http://localhost:9870/

resourcemanager webapp地址：
>http://localhost:8088/

创建mapreduce所需的用户文件夹：
```
hdfs dfs -mkdir /user
hdfs dfs -mkdir /user/zzy
```
上传配置文件到hdfs：
```
hdfs dfs -mkdir input
hdfs dfs -put /opt/hadoop/etc/hadoop/*.xml input
```
运行例程：
```
hadoop jar /opt/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.1.0.jar grep input output 'dfs[a-z.]+'
```
检查输出：
```
hdfs dfs -cat output/*
```
停止集群：
```
stop-dfs.sh && stop-yarn.sh
```
## 1.3 安装Hive
```
tar -zxf apache-hive-2.3.3-bin.tar.gz
ln -s apache-hive-2.3.3-bin hive

vi ~/.bash_profile
export HIVE_HOME=/opt/hive
export PATH=$PATH:$HIVE_HOME/bin

source ~/.bash_profile

hive
```
解决重复binding slf4j：
```
sudo mkdir /opt/hive/lib/backup
sudo mv /opt/hive/lib/log4j-slf4j-impl-2.6.2.jar /opt/hive/lib/backup
```
安装mysql：
```
```
