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
创建文件存放路径：
```
mkdir -p mydata/hdfs/namenode
mkdir -p mydata/hdfs/datanode
mkdir -p mydata/hdfs/tmp
chmod -R 755 mydata/hdfs/namenode
chmod -R 755 mydata/hdfs/datanode
chmod -R 755 mydata/hdfs/tmp
```
配置伪分布模式：
```
vi /opt/hadoop/etc/hadoop/core-site.xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
    <!-- 配置临时文件路径 -->
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/home/zzy/mydata/hdfs/tmp</value>
    </property>
</configuration>

vi /opt/hadoop/etc/hadoop/hdfs-site.xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <!-- 配置文件存储路径 -->
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/home/zzy/mydata/hdfs/namenode</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>/home/zzy/mydata/hdfs/datanode</value>
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
### 1.3.1 安装mysql
```
sudo apt-get install mysql-server
sudo apt install mysql-client
sudo apt install libmysqlclient-dev
```
测试安装是否成功：
```
sudo apt-get install net-tools
sudo netstat -tap | grep mysql
```
安装后初次登录，并设置root密码：
https://www.pomelolee.com/815.html
```
sudo cat /etc/mysql/debian.cnf
host     = localhost
user     = debian-sys-maint
password = d47tE2YfUktxbRXZ
socket   = /var/run/mysqld/mysqld.sock

mysql -udebian-sys-maint -p

grant all privileges on *.* to 'root'@'localhost' identified by 'password';
flush privileges;
```
解决access denied问题：
```
use mysql;
select user, plugin, authentication_string  from user;
delete from user where user='root' and plugin='auth_socket';

sudo service mysql restart;

mysql_upgrade -uroot -p
```
创建一个hive用户：
```
GRANT ALL ON *.* TO 'hive'@'%' IDENTIFIED BY 'hive';
GRANT ALL ON *.* TO 'hive'@'localhost' IDENTIFIED BY 'hive';
FLUSH PRIVILEGES;
```
### 1.3.2 Hive配置
添加mysql-connector-j驱动：
```
sudo mv mysql-connector-java-5.1.46-bin.jar  /opt/hive/lib/
```
创建目录：
```
hadoop fs -mkdir       /tmp
hadoop fs -mkdir -p    /user/hive/warehouse
hadoop fs -chmod g+w   /tmp
hadoop fs -chmod g+w   /user/hive/warehouse
```
修改hive-site.xml配置文件：
```
sudo vi /opt/hive/conf/hive-site.xml

<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>hive</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>hive</value>
    </property>
</configuration>
```
修改hive-env.sh变量配置：
```
cd /opt/hive/conf
sudo cp hive-env.sh.template hive-env.sh

sudo vi hive-env.sh
HADOOP_HOME=/opt/hadoop
export HIVE_CONF_DIR=/opt/hive/conf
```
自行生成metadata：
```
schematool -dbType mysql -initSchema
```
运行hive：
```
hive
```